import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubtitlesEditor {
    public static void delay(final String in, final String out,int delay, int fps) throws Exception {
        File inFile = new File("./" + in);
        File outFile = new File("./" + out);

        FileInputStream fstream = new FileInputStream(inFile);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        int frameDelay = delay * fps / 1000;

        PrintWriter pw = new PrintWriter(new FileOutputStream(outFile),true);

        Pattern checkLine = Pattern.compile("^\\{[0-9]+\\}\\{[0-9]+\\}.+$");

        Pattern getNumbers = Pattern.compile("\\d+");
        String getArgs = "\\{[0-9]+\\}";

        int[] frames = new int[2];
        String strLine;
        Matcher m = getNumbers.matcher(br.readLine());

        if(m.find())
        {
            int firstFrame = Integer.parseInt(m.group());
            if(firstFrame + frameDelay < 0) {
                pw.close();
                br.close();
                throw new Exception("Cannot translate that amount of miliseconds, first subtitle starts too fast!!");
            }
        }

        br.close();
        fstream = new FileInputStream(inFile);
        br = new BufferedReader(new InputStreamReader(fstream));

        for(int line=1; (strLine = br.readLine()) != null; ++line)
        {
            m = checkLine.matcher(strLine);
            if(!m.matches())
            {
                pw.close();
                br.close();
                throw new Exception("Line " + line + " does not match the pattern. Arguments probably have wrong format"+ "\n \"" + strLine + "\"");
            }


            m = getNumbers.matcher(strLine);
            for(int i=0; i<2; ++i){
                if(!m.find()) {
                    pw.close();
                    br.close();
                    throw new Exception("Not enought arguments at line: " + line+ "\n \"" + strLine + "\"");
                }

                frames[i] = Integer.parseInt(m.group());
            }
            if(frames[0] > frames[1]) {
                pw.close();
                br.close();
                throw new Exception("Invalid start, stop frames values at line: " + line + "\n \"" + strLine + "\"");
            }


            frames[0] += frameDelay;
            frames[1] += frameDelay;
            pw.append("{" + frames[0] + "}{" + frames[1] + "}" + strLine.replaceAll(getArgs, "")+"\n");
        }

        pw.close();
        br.close();
    }

}
