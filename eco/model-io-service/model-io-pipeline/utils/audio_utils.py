# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
import base64
import tempfile


def numpy_to_base64_wav(np_array, sample_rate=44100):
    import soundfile as sf
    base64_encoded_wav = None
    with tempfile.NamedTemporaryFile(suffix='.wav', delete=True) as temp_audio_file:
        temp_filename = temp_audio_file.name
        sf.write(temp_filename, np_array.squeeze(), sample_rate)
        audio_binary = temp_audio_file.read()
        base64_encoded_wav = base64.b64encode(audio_binary).decode('utf-8')

    return base64_encoded_wav
