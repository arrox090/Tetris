package audio;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class Audio {

  private static File[] audioFiles = new File[5];
  private Clip clip;

  // Static initializer block to initialize the audio files
  static {
    audioFiles[0] = new File("Audio//music.wav");
    audioFiles[1] = new File("Audio//lineDeletion.wav");
    audioFiles[2] = new File("Audio//blockPlace.wav");
  }

  public Audio() throws LineUnavailableException {}

  // Non-static method to play music (with looping capability)
  public void playMusic(int index, float volume) {
    if (index < 0 || index >= audioFiles.length) {
      System.err.println("Invalid audio file index");
      return;
    }
    try {
      // Stop and close the current clip if it's playing
      if (clip != null && clip.isRunning()) {
        clip.stop();
        clip.close();
      }

      // Create an AudioInputStream from the file
      AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFiles[index]);

      // Get a Clip object
      clip = AudioSystem.getClip();

      // Open the audio clip and load samples from the audio input stream
      clip.open(audioStream);

      setVolume(clip, volume);

      // Start playing the music
      clip.start();
      clip.loop(Clip.LOOP_CONTINUOUSLY); // Loop the music indefinitely

      // Close the audio stream as it's no longer needed
      audioStream.close();
    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
      // Handle exceptions internally
      e.printStackTrace();
    }
  }

  public static void playEffect(int index) {
    if (index < 0 || index >= audioFiles.length) {
      System.err.println("Invalid audio file index");
      return;
    }
    new Thread(() -> {
      try {
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFiles[index]);
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);

        clip.start();
      } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
        e.printStackTrace();
      }
    }).start();
  }


  // Method to stop the music
  public void stopMusic() {
    if (clip != null && clip.isRunning()) {
      clip.stop();
      clip.close();
    }
  }

  public void resumeMusic() {
    clip.start();
  }

  public void resetMusic() {
    clip.setFramePosition(0);
    clip.start();
  }

  // Method to set the volume (gain) of the clip
  private static void setVolume(Clip clip, float volume) {
    try {
      // Get the FloatControl for volume control
      FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
      // Set the volume (volume should be between gainControl.getMinimum() and gainControl.getMaximum())
      gainControl.setValue(volume);
    } catch (IllegalArgumentException e) {
      // Handle case where volume adjustment is not supported
      System.err.println("Volume adjustment not supported for this clip.");
    }
  }
}
