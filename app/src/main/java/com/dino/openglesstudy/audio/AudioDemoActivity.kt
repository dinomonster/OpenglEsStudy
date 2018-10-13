package com.dino.openglesstudy.audio

import android.Manifest
import com.dino.openglesstudy.R
import com.dino.openglesstudy.base.BaseActivity
import android.os.AsyncTask
import android.os.Environment.DIRECTORY_MUSIC
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.os.Build
import android.media.*
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import com.dino.openglesstudy.audio.AudioConfig.AUDIO_FORMAT
import com.dino.openglesstudy.audio.AudioConfig.CHANNEL_CONFIG
import com.dino.openglesstudy.audio.AudioConfig.SAMPLE_RATE_INHZ
import kotlinx.android.synthetic.main.audio_activity.*
import java.io.*


class AudioDemoActivity : BaseActivity(), View.OnClickListener {
    private val MY_PERMISSIONS_REQUEST = 1001
    private val TAG = "jqd"

    private var mBtnControl: Button? = null
    private var mBtnPlay: Button? = null

    /**
     * 需要申请的运行时权限
     */
    private val permissions = arrayOf<String>(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    /**
     * 被用户拒绝的权限列表
     */
    private val mPermissionList = ArrayList<String>()
    private var isRecording: Boolean = false
    private var audioRecord: AudioRecord? = null
    private var mBtnConvert: Button? = null
    private var audioTrack: AudioTrack? = null
    private var audioData: ByteArray? = null
    private var fileInputStream: FileInputStream? = null

    override fun setViewId(): Int {
        return R.layout.audio_activity
    }

    override fun initData() {
        super.initData()
        btn_control?.setOnClickListener(this)
        btn_convert?.setOnClickListener(this)
        btn_play?.setOnClickListener(this)
        checkPermissions()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_control -> {
                val button = view as Button
                if (button.text.toString() == getString(R.string.start_record)) {
                    button.text = getString(R.string.stop_record)
                    startRecord()
                } else {
                    button.text = getString(R.string.start_record)
                    stopRecord()
                }
            }
            R.id.btn_convert -> {
                val pcmToWavUtil = PcmToWavUtil(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT)
                val pcmFile = File(getExternalFilesDir(DIRECTORY_MUSIC), "test.pcm")
                val wavFile = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.wav")
                if (!wavFile.mkdirs()) {
                    Log.e(TAG, "wavFile Directory not created")
                }
                if (wavFile.exists()) {
                    wavFile.delete()
                }
                pcmToWavUtil.pcmToWav(pcmFile.absolutePath, wavFile.absolutePath)
            }
            R.id.btn_play -> {
                val btn = view as Button
                val string = btn.text.toString()
                if (string == getString(R.string.start_play)) {
                    btn.text = getString(R.string.stop_play)
                    playInModeStream()
                    //playInModeStatic();
                } else {
                    btn.text = getString(R.string.start_play)
                    stopPlay()
                }
            }

            else -> {
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == MY_PERMISSIONS_REQUEST) {
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, permissions[i] + " 权限被用户禁止！")
                }
            }
            // 运行时权限的申请不是本demo的重点，所以不再做更多的处理，请同意权限申请。
        }
    }


    fun startRecord() {
        val minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT)
        audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_INHZ,
                CHANNEL_CONFIG, AUDIO_FORMAT, minBufferSize)

        val data = ByteArray(minBufferSize)
        val file = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.pcm")
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created")
        }
        if (file.exists()) {
            file.delete()
        }

        audioRecord?.startRecording()
        isRecording = true

        // TODO: 2018/3/10 pcm数据无法直接播放，保存为WAV格式。

        Thread(Runnable {
            var os: FileOutputStream? = null
            try {
                os = FileOutputStream(file)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            if (null != os) {
                while (isRecording) {
                    val read = audioRecord!!.read(data, 0, minBufferSize)
                    // 如果读取音频数据没有出现错误，就将数据写入到文件
                    if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                        try {
                            os!!.write(data)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }
                }
                try {
                    Log.i(TAG, "run: close file output stream !")
                    os!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }).start()
    }


    fun stopRecord() {
        isRecording = false
        // 释放资源
        if (null != audioRecord) {
            audioRecord!!.stop()
            audioRecord!!.release()
            audioRecord = null
            //recordingThread = null;
        }
    }


    private fun checkPermissions() {
        // Marshmallow开始才用申请运行时权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (i in permissions.indices) {
                if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i])
                }
            }
            if (!mPermissionList.isEmpty()) {
                val permissions = mPermissionList.toTypedArray()
                ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST)
            }
        }
    }


    /**
     * 播放，使用stream模式
     */
    private fun playInModeStream() {
        /*
        * SAMPLE_RATE_INHZ 对应pcm音频的采样率
        * channelConfig 对应pcm音频的声道
        * AUDIO_FORMAT 对应pcm音频的格式
        * */
        val channelConfig = AudioFormat.CHANNEL_OUT_MONO
        val minBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE_INHZ, channelConfig, AUDIO_FORMAT)
        audioTrack = AudioTrack(
                AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build(),
                AudioFormat.Builder().setSampleRate(SAMPLE_RATE_INHZ)
                        .setEncoding(AUDIO_FORMAT)
                        .setChannelMask(channelConfig)
                        .build(),
                minBufferSize,
                AudioTrack.MODE_STREAM,
                AudioManager.AUDIO_SESSION_ID_GENERATE)
        audioTrack!!.play()

        val file = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.pcm")
        try {
            fileInputStream = FileInputStream(file)
            Thread(Runnable {
                try {
                    val tempBuffer = ByteArray(minBufferSize)
                    while (fileInputStream!!.available() > 0) {
                        val readCount = fileInputStream!!.read(tempBuffer)
                        if (readCount == AudioTrack.ERROR_INVALID_OPERATION || readCount == AudioTrack.ERROR_BAD_VALUE) {
                            continue
                        }
                        if (readCount != 0 && readCount != -1) {
                            audioTrack!!.write(tempBuffer, 0, readCount)
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }).start()

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


    /**
     * 播放，使用static模式
     */
    private fun playInModeStatic() {
        // static模式，需要将音频数据一次性write到AudioTrack的内部缓冲区

        object : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg params: Void): Void? {
                try {
                    val ins = resources.openRawResource(R.raw.ding)
                    try {
                        val out = ByteArrayOutputStream()
                        var b: Int = ins.read()
                        while (b != -1) {
                            out.write(b)
                        }
                        Log.d(TAG, "Got the data")
                        audioData = out.toByteArray()
                    } finally {
                        ins.close()
                    }
                } catch (e: IOException) {
                    Log.wtf(TAG, "Failed to read", e)
                }

                return null
            }


            override fun onPostExecute(v: Void) {
                Log.i(TAG, "Creating track...audioData.length = " + audioData!!.size)

                // R.raw.ding铃声文件的相关属性为 22050Hz, 8-bit, Mono
                audioTrack = AudioTrack(
                        AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build(),
                        AudioFormat.Builder().setSampleRate(22050)
                                .setEncoding(AudioFormat.ENCODING_PCM_8BIT)
                                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                .build(),
                        audioData!!.size,
                        AudioTrack.MODE_STATIC,
                        AudioManager.AUDIO_SESSION_ID_GENERATE)
                Log.d(TAG, "Writing audio data...")
                audioTrack!!.write(audioData!!, 0, audioData!!.size)
                Log.d(TAG, "Starting playback")
                audioTrack!!.play()
                Log.d(TAG, "Playing")
            }

        }.execute()

    }


    /**
     * 停止播放
     */
    private fun stopPlay() {
        if (audioTrack != null) {
            Log.d(TAG, "Stopping")
            audioTrack!!.stop()
            Log.d(TAG, "Releasing")
            audioTrack!!.release()
            Log.d(TAG, "Nulling")
        }
    }
}