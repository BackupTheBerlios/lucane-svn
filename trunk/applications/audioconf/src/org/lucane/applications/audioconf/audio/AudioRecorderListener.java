package org.lucane.applications.audioconf.audio;

public interface AudioRecorderListener
{
	public void audioRecordingStarted();
	public void audioRecorded(byte[] data);
	public void audioRecordingEnded();
}