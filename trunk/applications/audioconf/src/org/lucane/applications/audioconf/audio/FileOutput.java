package org.lucane.applications.audioconf.audio;

import java.io.*;

public class FileOutput implements AudioListener
{
	private String filename;
	private FileOutputStream output;
	
	public FileOutput(String filename)
	{
		this.filename = filename;
	}
	
	public void audioRecordingStarted() 
	{
		try {
			this.output = new FileOutputStream(this.filename);
			System.out.println("recording 5 seconds...");			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void audioRecorded(byte[] data) 	
	{
		try {
			this.output.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void audioRecordingEnded() 
	{
		try {
			this.output.close();
			System.out.println("ended.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	throws Exception
	{
		FileOutput fo = new FileOutput("test.spx");
		AudioRecorder ar = new AudioRecorder();
		ar.addAudioListener(fo);
		
		Thread t = new Thread(ar);
		
		t.start();
		Thread.sleep(5*1000);
		ar.stop();
		
		System.out.println(t.isAlive());
	}
}