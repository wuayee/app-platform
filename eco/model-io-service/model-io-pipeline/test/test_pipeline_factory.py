# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
import sys
import unittest
from unittest.mock import MagicMock

import set_path
from pipeline_factory import PipelineFactory
from pipelines import NormalPipeline, TextToImagePipeline, ImageToImagePipeline, TextToSpeechPipeline


def mock_pipeline_package():
    diffuser_module_str = 'diffusers'
    soundfile_module_str = 'soundfile'
    sys.modules[diffuser_module_str] = MagicMock()
    sys.modules[soundfile_module_str] = MagicMock()


class TestPipelineFactory(unittest.TestCase):
    def test_get_pipeline(self):
        mock_pipeline_package()
        self.assertEqual(PipelineFactory.get_pipeline("text-to-speech"), TextToSpeechPipeline)
        self.assertEqual(PipelineFactory.get_pipeline("image-to-image"), ImageToImagePipeline)
        self.assertEqual(PipelineFactory.get_pipeline("text-to-image"), TextToImagePipeline)
        self.assertEqual(PipelineFactory.get_pipeline("document-question-answering"), NormalPipeline)
