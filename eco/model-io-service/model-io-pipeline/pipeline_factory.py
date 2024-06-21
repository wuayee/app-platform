# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.

from pipelines import NormalPipeline, TextToImagePipeline, ImageToImagePipeline, TextToSpeechPipeline, \
    ImageSegementationPipeline, MaskGenerationPipeline

IMAGE_TO_IMAGE = "image-to-image"
TEXT_TO_IMAGE = "text-to-image"
TEXT_TO_SPEECH = "text-to-speech"
TEXT_TO_AUDIO = "text-to-audio"
IMAGE_SEGMENTATION = "image-segmentation"
MASK_GENERATION = "mask-generation"


class PipelineFactory:
    @classmethod
    def get_pipeline(cls, task: str):
        pipeline_dict = {
            IMAGE_TO_IMAGE: ImageToImagePipeline, TEXT_TO_IMAGE: TextToImagePipeline,
            TEXT_TO_SPEECH: TextToSpeechPipeline, TEXT_TO_AUDIO: TextToSpeechPipeline,
            IMAGE_SEGMENTATION: ImageSegementationPipeline, MASK_GENERATION: MaskGenerationPipeline,
        }
        return pipeline_dict.get(task, NormalPipeline)
