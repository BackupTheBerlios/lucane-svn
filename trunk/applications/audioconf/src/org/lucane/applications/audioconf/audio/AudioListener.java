package org.lucane.applications.audioconf.audio;
public interface AudioListener
{
	public void audioRecordingStarted();
	public void audioRecorded(byte[] data);
	public void audioRecordingEnded();
}