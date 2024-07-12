# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
from transformers import pipeline, Text2TextGenerationPipeline
from utils.image_utils import convert_image, get_image
from utils.audio_utils import numpy_to_base64_wav
from utils.env_utils import ENVTOOLS
from utils.param_utils import convert_numpy_data


class NormalPipeline:
    def __init__(self, model, task):
        self._pipeline = pipeline(task=task, model=model, device=ENVTOOLS.select_device())

    def call(self, args):
        if isinstance(self._pipeline, Text2TextGenerationPipeline):
            rsp = self._pipeline(args.pop('inputs'), **args)
        else:
            rsp = self._pipeline(**args)
        return convert_numpy_data(rsp)


class ImageToImagePipeline:
    def __init__(self, model, task):
        from diffusers import StableDiffusionImg2ImgPipeline
        self._pipeline = StableDiffusionImg2ImgPipeline.from_pretrained(model)
        self._pipeline.to(ENVTOOLS.select_device(force=True))

    def call(self, args):
        args["image"] = get_image(args["image"])
        image_list = self._pipeline(**args)
        result = []
        for image in image_list.images:
            result.append({"data": convert_image(image), "mime": 'image/png'})
        return result


class TextToImagePipeline:
    def __init__(self, model, task):
        from diffusers import DiffusionPipeline
        self._pipeline = DiffusionPipeline.from_pretrained(model)
        self._pipeline.to(ENVTOOLS.select_device(force=True))

    def call(self, args):
        image_list = self._pipeline(**args)
        result = []
        for image in image_list.images:
            result.append({"data": convert_image(image), "mime": 'image/png'})
        return result


class TextToSpeechPipeline:
    def __init__(self, model, task):
        self._pipeline = pipeline(task=task, model=model, device=ENVTOOLS.select_device())

    def call(self, args):
        response = self._pipeline(**args)
        audio = response['audio']
        sampling_rate = response["sampling_rate"]

        result = {
            "media": {
                "data": numpy_to_base64_wav(audio, sampling_rate),
                "mime": 'audio/wav'
            },
            "sampling_rate": sampling_rate
        }
        return result


class ImageSegementationPipeline:
    def __init__(self, model, task):
        self._pipeline = pipeline(task=task, model=model, device=ENVTOOLS.select_device())

    def call(self, args):
        result = self._pipeline(**args)
        result["mask"] = {"data": convert_image(result.data), "mime": 'image/png'}
        return result


class MaskGenerationPipeline:
    def __init__(self, model, task):
        self._pipeline = pipeline(task=task, model=model, device=ENVTOOLS.select_device())

    def __call__(self, args):
        result = self._pipeline(**args)
        result["mask"] = {"data": convert_image(result.data), "mime": 'image/png'}
        return result
