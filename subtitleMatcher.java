import java.io.File;
import java.io.FilenameFilter;
import java.util.Scanner;
import java.util.regex.*;

public class subtitleMatcher {
    public static void main(String[] args) {
        String videoFormat, subFormat;
        if (args.length == 2) {
            System.out.println("Oooo got some arguments here!");
            videoFormat = "." + args[0];
            subFormat = "." + args[1];
        } else {
            System.out.println("Usage: $ java subtitleMatcher [videoFile Format] [subtitleFile format]");
            System.out.println("No args provided. Do you want to choose mkv and srt as default?");
            System.out.println("[Y/y] -->  Take mkv and srt as formats.");
            System.out.println("[N/n] -->  I would like to provide formats myself.");
            Scanner in = new Scanner(System.in);
            String choice = in.nextLine();
            
            if (choice.toUpperCase().equals("Y")) {
                videoFormat = ".mkv";
                subFormat = ".srt";
            } else if(choice.toUpperCase().equals("N")) {
                System.out.print("Video format: ");
                videoFormat = "."+in.nextLine();
                System.out.print("Subtitle format: ");
                subFormat = "."+in.nextLine();
            } else {
                System.out.println("Wrong choice.\nExiting...Bye.");
                return;
            }
        }
        System.out.println("Video Format: "+videoFormat + "\nSubtitle format: "+subFormat);
        System.out.println("Looking for video and subtitle files in " + System.getProperty("user.dir"));
        String path = System.getProperty("user.dir");
        File currDir = new File(path);
        FilenameFilter videoFilenameFilter = new FilenameFilter() {

            @Override
            public boolean accept(File arg0, String arg1) {
                String lowercaseName = arg1.toLowerCase();
                if (lowercaseName.endsWith(videoFormat)) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        FilenameFilter subFilenameFilter = new FilenameFilter() {

            @Override
            public boolean accept(File arg0, String arg1) {
                String lowercaseName = arg1.toLowerCase();
                if (lowercaseName.endsWith(subFormat)) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        File videos[] = currDir.listFiles(videoFilenameFilter);
        File subs[] = currDir.listFiles(subFilenameFilter);
        for (int i = 0; i < videos.length; i++) {
            for (int j = 0; j < subs.length; j++) {
                // Actual regex: .*S\\d{2}E\\d{2}\\b.*
                Pattern pattern = Pattern.compile("E\\d{1,2}");
                Matcher videoMatcher = pattern.matcher(videos[i].getName());
                Matcher subMatcher = pattern.matcher(subs[j].getName());
                if (subMatcher.find() && videoMatcher.find()) {
                    if (subs[j].getName().substring(subMatcher.start(), subMatcher.start() + 3)
                            .equals(videos[i].getName().substring(videoMatcher.start(), videoMatcher.start() + 3))) {
                        System.out.println();
                        System.out.println("Found [" + videos[i].getName() + "," + subs[j].getName()+"]");
                        if (videos[i].getName().substring(0, videos[i].getName().lastIndexOf("."))
                                .equals(subs[j].getName().substring(0, subs[j].getName().lastIndexOf(".")))) {
                            System.out.println(
                                    "Same file name found for both video and subtitle files. No need to rename.");
                            break;
                        }
                        System.out.println("Renaming file...");
                        if (subs[j].renameTo(new File(
                                videos[i].getName().substring(0, videos[i].getName().lastIndexOf(".")) + subFormat))) {
                            System.out.println("Renamed " + subs[j].getName() + " to "
                                    + videos[i].getName().substring(0, videos[i].getName().lastIndexOf("."))
                                    + videoFormat);
                        } else {
                            System.err.println("Error in renaming files.");
                        }
                        break;
                    }
                }
            }
        }
    }
}