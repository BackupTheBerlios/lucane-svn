package org.lucane.applications.audioconf.audio;
import java.util.*;
import java.io.*;

import javax.sound.sampled.*;
import org.xiph.speex.spi.*;

public class AudioRecorder implements Runnable
{
	public static final int AUDIO_FREQUENCY = 8000;
	
	private AudioInputStream audioStream;
	private TargetDataLine dataLine;
	private AudioFormat audioFormat;
	
	private ArrayList listeners;
	
	public AudioRecorder()
	{
		this.listeners = new ArrayList();
		
		this.audioFormat = new AudioFormat(
				AudioFormat.Encoding.PCM_SIGNED,
				AUDIO_FREQUENCY, 16, 2, 4, AUDIO_FREQUENCY, false);

		DataLine.Info	info = new DataLine.Info(TargetDataLine.class, audioFormat);
		try {
			dataLine = (TargetDataLine) AudioSystem.getLine(info);
		} catch (LineUnavailableException lue) {
			lue.printStackTrace();
		}

		audioStream = new Pcm2SpeexAudioInputStream(new AudioInputStream(dataLine), audioFormat,
				AudioSystem.NOT_SPECIFIED);
	}
	
	public void addAudioListener(AudioRecorderListener listener)
	{
		this.listeners.add(listener);
	}
	
	public void removeAudioListener(AudioRecorderListener listener)
	{
		this.listeners.remove(listener);
	}

	public void stop()
	{
		
		try {
			dataLine.stop();
			dataLine.close();
			audioStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Iterator listeners = this.listeners.iterator();
		while(listeners.hasNext())
			((AudioRecorderListener)listeners.next()).audioRecordingEnded();
	}
	
	public void run()
	{
		try {
			dataLine.open();
			dataLine.start();
		} catch(LineUnavailableException lue) {
			lue.printStackTrace();
		}

		Iterator listeners = this.listeners.iterator();
		while(listeners.hasNext())
			((AudioRecorderListener)listeners.next()).audioRecordingStarted();
		
		
		try
		{
			byte[] data = new byte[1024];
			while(dataLine.isOpen())
			{	
				audioStream.read(data);
				
				listeners = this.listeners.iterator();
				while(listeners.hasNext())
					((AudioRecorderListener)listeners.next()).audioRecorded(data);				
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}