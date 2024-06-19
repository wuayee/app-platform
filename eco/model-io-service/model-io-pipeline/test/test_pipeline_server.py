# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
import unittest
import json
from fastapi.testclient import TestClient
import pipeline_server
from pipeline_server import app


class MockPipeline:
    def __init__(self):
        self.return_value = {"output": "pipeline test passed"}

    def call(self, args):
        return self.return_value


class TestPipelineServer(unittest.TestCase):

    def test_health_live(self):
        client = TestClient(app)
        response = client.get("/v2/health/live")
        self.assertEqual(response.status_code, 200)

    def test_health_ready(self):
        client = TestClient(app)
        response = client.get("/v2/health/ready")
        self.assertEqual(response.status_code, 200)

    def test_use_pipeline(self):
        pipeline_server.hf_pipeline = MockPipeline()
        client = TestClient(app)
        response = client.post("/v1/huggingface/pipeline", json={"args": {"inputs": "This is a test for pipeline"}})
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.content, json.dumps(
            pipeline_server.hf_pipeline.return_value,
            ensure_ascii=False,
            allow_nan=False,
            indent=None,
            separators=(",", ":"),
        ).encode("utf-8"))
