README.txt

Cauchy Choi
Elizabeth Deng

CSCI576 Final Project

FILE PATHS ARE LOCAL TO OUR MACHINE. IF YOU WANT TO RUN THEM, PLEASE CHANGE THE FILE PATHS IN:
-VideoIndexer.java
-ColorIndexer.java
-AudioIndexer.java
-MotionIndexer.java

There are 3 pre-processing layers to this project to extract the descriptors:
Color - The dominant color is processed from each frame and compared to the dominant color of frames in the other videos. The color is calculated based on hue, saturation, value so sometimes factors such as brightness may affect the color preprocess.
Audio - The maximum amplitude per video frame is extracted and compared to the maximum amplitude of frames in the other videos.
Motion - Macroblocks are used to compare successive frames. The differences are calculated and averaged per frame to quantify the motion per frame which are then compared to the motion of frames in the other videos.

Notes:
-The program takes a long time to start up since it has to load all 12 videos. Be patient! And increase the virtual memory if you get java heap or out of memory errors.
-Sliders are implemented on the UI as weights for each descriptor. If you want to only search by color, for example, you could slide the audio and motion sliders to 0 while leaving the color at 1 or 2.
-Clicking on a frame in the slider view will set that video as the current video and jump straight to that frame. This is also how you switch what the current video is.
-When a video hits the end, you have to click stop and then play again to restart it or click on the first frame of the video in the slider view.
-Videos are listed in order of similarity and the most similar frame is highlighted for each video.
-For each video, the closest frame to the target parameters is highlighted, but the closest frame may not necessarily be related to the original video much at all if the videos are too different to begin with.
-The matches may not be logically correct, but should be reasonably accurate based on the search contexts used.
