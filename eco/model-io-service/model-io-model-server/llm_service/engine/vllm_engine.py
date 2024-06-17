# -*- coding: utf-8 -*-
# !/usr/bin/python
# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
import logging
import time
from typing import Union, AsyncGenerator, AsyncIterator
from dataclasses import asdict
from http import HTTPStatus
from fastapi import Request
from llm_service.utils.protocol import (ErrorResponse, ChatCompletionRequest, ChatCompletionResponse, UsageInfo,
                                        ChatCompletionResponseChoice, ChatMessage, ChatCompletionResponseStreamChoice,
                                        DeltaMessage, ChatCompletionStreamResponse)
from llm_service.engine.engine_config import EngineConfig
from llm_service.engine.function_calling import FunctionCallFactory
from llm_service.utils.message_utils import preprocess_fncall_messages, prepend_tools_system, \
    postprocess_fncall_messages
from llm_service.utils.request_utils import random_uuid

logger = logging.getLogger(__name__)


class VllmEngine:
    def __init__(self, engine_config: EngineConfig, response_role: str):
        self.engine_config = engine_config
        self.response_role = response_role
        self.load()

    @staticmethod
    def to_sampling_params(vllm_request: ChatCompletionRequest):
        from vllm.sampling_params import SamplingParams
        return SamplingParams(
            n=vllm_request.n,
            presence_penalty=vllm_request.presence_penalty,
            frequency_penalty=vllm_request.frequency_penalty,
            repetition_penalty=vllm_request.repetition_penalty,
            temperature=vllm_request.temperature,
            top_p=vllm_request.top_p,
            min_p=vllm_request.min_p,
            stop=vllm_request.stop,
            stop_token_ids=vllm_request.stop_token_ids,
            max_tokens=vllm_request.max_tokens,
            best_of=vllm_request.best_of,
            top_k=vllm_request.top_k,
            ignore_eos=vllm_request.ignore_eos,
            use_beam_search=vllm_request.use_beam_search,
            skip_special_tokens=vllm_request.skip_special_tokens,
            spaces_between_special_tokens=vllm_request.spaces_between_special_tokens,
            include_stop_str_in_output=vllm_request.include_stop_str_in_output,
            length_penalty=vllm_request.length_penalty,
        )

    def load(self):
        from vllm.engine.arg_utils import AsyncEngineArgs
        from vllm.engine.async_llm_engine import AsyncLLMEngine
        from transformers import AutoConfig

        engine_args = AsyncEngineArgs(**asdict(self.engine_config))
        self.model_config = AutoConfig.from_pretrained(self.engine_config.model)
        self.engine = AsyncLLMEngine.from_engine_args(engine_args)
        self.fn_call_model = FunctionCallFactory.get_fncall_model(model_name=self.model_config.model_type)

    async def generate(self, vllm_request: ChatCompletionRequest, raw_request) -> Union[
        ErrorResponse, AsyncGenerator[str, None]]:
        if not vllm_request.tools:
            vllm_request.tool_choice = None
        messages = vllm_request.messages
        if vllm_request.tool_choice == "auto":
            messages = preprocess_fncall_messages(self.fn_call_model, vllm_request.messages)  # 预处理函数调用信息拼接
            messages = prepend_tools_system(self.fn_call_model, messages, vllm_request.tools)
        if isinstance(vllm_request.stop, str):
            vllm_request.stop = [vllm_request.stop]

        vllm_request.stop += self.fn_call_model.fn_stop_words
        try:
            prompt = self.engine.get_tokenizer().apply_chat_template(
                conversation=messages,
                tokenize=False,
                add_generation_prompt=vllm_request.add_generation_prompt).rstrip('\n')

        except Exception as e:
            logger.error("Error in applying chat template from request")
            return ErrorResponse(message="Error in applying chat template",
                                 type="BadRequestError",
                                 code=HTTPStatus.BAD_REQUEST)

        request_id = f"{random_uuid()}"
        try:
            token_ids = self.engine.get_tokenizer()(prompt).input_ids
        except ValueError as e:
            return ErrorResponse(message="Error in tokenizer prompt",
                                 type="InternalServerError",
                                 code=HTTPStatus.INTERNAL_SERVER_ERROR)

        sampling_params = self.to_sampling_params(vllm_request)
        lora_request = None

        result_generator = self.engine.generate(prompt, sampling_params,
                                                request_id, token_ids,
                                                lora_request)

        tool_stream = False

        pretext = ''
        if vllm_request.stream and vllm_request.tool_choice == "auto":
            pretext = await self._get_pretext(result_generator)
            if self.fn_call_model.fn_name in pretext:
                tool_stream = True

        # Streaming response
        if vllm_request.stream:
            if tool_stream:
                return self._chat_completion_stream_tool_generator(vllm_request, result_generator, request_id)
            return self._chat_completion_stream_generator(
                vllm_request, pretext, result_generator, request_id)
        else:
            return await self._chat_completion_full_generator(
                vllm_request, raw_request, result_generator, request_id)

    async def _get_pretext(self, result_generator):
        len_limit = len(self.fn_call_model.fn_name)
        pretext = ""
        async for res in result_generator:
            pretext = res.outputs[0].text
            if len(pretext) > len_limit:
                break
        return pretext

    async def _chat_completion_full_generator(
            self, request: ChatCompletionRequest, raw_request: Request,
            result_generator: AsyncIterator,
            request_id: str) -> Union[ErrorResponse, ChatCompletionResponse]:

        model_name = request.model
        created_time = int(time.monotonic())
        final_res = None

        async for res in result_generator:
            if await raw_request.is_disconnected():
                await self.engine.abort(request_id)
                return ErrorResponse(message="Client disconnect",
                                     type="RequestTimeOutError",
                                     code=HTTPStatus.REQUEST_TIMEOUT)
            final_res = res

        choices = []
        role = self.response_role
        for output in final_res.outputs:
            choice_data = ChatCompletionResponseChoice(
                index=output.index,
                message=postprocess_fncall_messages(self.fn_call_model, ChatMessage(role=role, content=output.text)),
                finish_reason=output.finish_reason,
            )
            choices.append(choice_data)

        num_prompt_tokens = len(final_res.prompt_token_ids)
        num_generated_tokens = sum(
            len(output.token_ids) for output in final_res.outputs)
        usage = UsageInfo(
            prompt_tokens=num_prompt_tokens,
            completion_tokens=num_generated_tokens,
            total_tokens=num_prompt_tokens + num_generated_tokens,
        )
        response = ChatCompletionResponse(
            id=request_id,
            created=created_time,
            model=model_name,
            choices=choices,
            usage=usage,
        )
        return response

    async def _chat_completion_stream_tool_generator(
            self, request: ChatCompletionRequest,
            result_generator: AsyncIterator, request_id: str
    ) -> Union[ErrorResponse, AsyncGenerator[str, None]]:
        data = None
        final_res = None
        model_name = request.model
        created_time = int(time.monotonic())
        chunk_object_type = "chat.completion.chunk"

        # Send first response for each request.n (index) with the role / 对于每个生成序列创建一个流
        previous_num_tokens = [0] * request.n

        async for res in result_generator:
            final_res = res

        for output in final_res.outputs:
            i = output.index

            delta_text = output.text
            previous_num_tokens[i] = len(output.token_ids)

            if output.finish_reason is not None:
                prompt_tokens = len(final_res.prompt_token_ids)
                final_usage = await self._create_usage(prompt_tokens=prompt_tokens,
                                                       previous_num_tokens=previous_num_tokens[i])
                choice_data = await self._create_tool_choice_data(role=None, index=i, delta_text=delta_text,
                                                                  finish_reason=output.finish_reason)
                chunk = await self._create_chunk(request_id=request_id, chunk_object_type=chunk_object_type,
                                                 created_time=created_time, choice_data=choice_data,
                                                 model_name=model_name)
                chunk.usage = final_usage
                data = chunk.model_dump_json(exclude_unset=True, exclude_none=True)

            yield f"data: {data}\n\n"
        yield "data: [DONE]\n\n"

    async def _chat_completion_stream_generator(
            self, request: ChatCompletionRequest, pretext: str,
            result_generator: AsyncIterator, request_id: str
    ) -> Union[ErrorResponse, AsyncGenerator[str, None]]:

        model_name = request.model
        created_time = int(time.monotonic())
        chunk_object_type = "chat.completion.chunk"

        # Send first response for each request.n (index) with the role / 对于每个生成序列创建一个流
        role = self.response_role
        previous_texts = [pretext] * request.n
        previous_num_tokens = [0] * request.n
        finish_reason_sent = [False] * request.n

        for i in range(request.n):
            choice_data = await self._create_choice_data(role=role, index=i, delta_text="", finish_reason=None)
            chunk = await self._create_chunk(request_id=request_id, chunk_object_type=chunk_object_type,
                                             created_time=created_time, choice_data=choice_data,
                                             model_name=model_name)
            data = chunk.model_dump_json(exclude_unset=True)
            yield f"data: {data}\n\n"
            choice_data = await self._create_choice_data(role=None, index=i, delta_text=pretext, finish_reason=None)
            chunk = await self._create_chunk(request_id=request_id, chunk_object_type=chunk_object_type,
                                             created_time=created_time, choice_data=choice_data,
                                             model_name=model_name)
            data = chunk.model_dump_json(exclude_unset=True)
            yield f"data: {data}\n\n"

        async for res in result_generator:
            for output in res.outputs:
                i = output.index

                if finish_reason_sent[i]:
                    continue

                delta_text = output.text[len(previous_texts[i]):]
                previous_texts[i] = output.text
                previous_num_tokens[i] = len(output.token_ids)

                if output.finish_reason is None:
                    choice_data = await self._create_choice_data(role=None, index=i, delta_text=delta_text,
                                                                 finish_reason=None)
                    chunk = await self._create_chunk(request_id=request_id, chunk_object_type=chunk_object_type,
                                                     created_time=created_time, choice_data=choice_data,
                                                     model_name=model_name)
                    data = chunk.model_dump_json(exclude_unset=True)
                else:
                    prompt_tokens = len(res.prompt_token_ids)
                    final_usage = await self._create_usage(prompt_tokens=prompt_tokens,
                                                           previous_num_tokens=previous_num_tokens[i])
                    choice_data = await self._create_choice_data(role=None, index=i, delta_text=delta_text,
                                                                 finish_reason=output.finish_reason)
                    chunk = await self._create_chunk(request_id=request_id, chunk_object_type=chunk_object_type,
                                                     created_time=created_time, choice_data=choice_data,
                                                     model_name=model_name)
                    chunk.usage = final_usage
                    data = chunk.model_dump_json(exclude_unset=True, exclude_none=True)
                    finish_reason_sent[i] = True
                yield f"data: {data}\n\n"
        yield "data: [DONE]\n\n"

    async def _create_usage(self, prompt_tokens, previous_num_tokens):
        return UsageInfo(
            prompt_tokens=prompt_tokens,
            completion_tokens=previous_num_tokens,
            total_tokens=prompt_tokens + previous_num_tokens,
        )

    async def _create_choice_data(self, role, index, delta_text, finish_reason):
        return ChatCompletionResponseStreamChoice(
            index=index,
            delta=DeltaMessage(role=role, content=delta_text),
            finish_reason=finish_reason)

    async def _create_tool_choice_data(self, role, index, delta_text, finish_reason):
        return ChatCompletionResponseStreamChoice(
            index=index,
            delta=postprocess_fncall_messages(self.fn_call_model, ChatMessage(role=role, content=delta_text)),
            finish_reason=finish_reason)

    async def _create_chunk(self, request_id, chunk_object_type, created_time, choice_data, model_name):
        return ChatCompletionStreamResponse(
            id=request_id,
            object=chunk_object_type,
            created=created_time,
            choices=[choice_data],
            model=model_name)
