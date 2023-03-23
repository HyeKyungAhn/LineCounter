import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class LineCounter {
    final static int INVALID_INPUT = 0;
    final static int NOT_EXISTING_RESOURCE = 1;
    final static int EMPTY_DIRECTORY = 2;

    public static void main(String[] args) {
        if(args.length <= 0 || args.length > 2){
            printInfo(INVALID_INPUT);
            return;
        }

        String resourcePath = args[0];
        String excludeRegex = "";
        File resource = new File(resourcePath);
        List<Map<String, Object>> fileLineInfoList = new ArrayList<>();
        Pattern regexPattern = null;

        if(!resource.exists()){
            printInfo(NOT_EXISTING_RESOURCE);
            return;
        }

        if(resource.isDirectory()){
            if(args.length == 2){
                excludeRegex = args[1];
                regexPattern = Pattern.compile(excludeRegex);
            }

            File[] fileList = resource.listFiles();

            if(fileList == null){
                printInfo(EMPTY_DIRECTORY);
                return;
            }

            for(File file : fileList){
                if(!excludeRegex.equals("") &&  regexPattern.matcher(file.getName()).find()){
                    continue;
                }
                fileLineInfoList.add(countLine(file));
            }
        } else {
            fileLineInfoList.add(countLine(resource));
        }

        fileLineInfoList.add(countTotalLine(fileLineInfoList));
        print(fileLineInfoList);
    }

    private static Map<String, Object> countLine(File file){
        Map<String, Object> map = new HashMap<>();
        int codeLineSum = 0;
        int blankLinSum = 0;
        int commentLineSum = 0;
        String checkCommentsRegex = "^\\/[/*]|^[\\t ]+\\/[/*]"; // // or /*
        Pattern regexPattern = Pattern.compile(checkCommentsRegex);

        try {
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String currentLine;

            while((currentLine = reader.readLine()) != null){
                if(currentLine.trim().isBlank()){
                    blankLinSum++;
                    continue;
                } else if(regexPattern.matcher(currentLine).find()){
                    commentLineSum++;
                    continue;
                }
                codeLineSum++;
            }

        } catch (IOException e) {
            System.out.println("해당 파일 파일("+file.getName()+")은 정상적으로 읽을 수 없습니다.");
        }

        map.put("fileName", file.getName());
        map.put("codeSum", codeLineSum);
        map.put("blankSum", blankLinSum);
        map.put("commentSum", commentLineSum);

        return map;
    }

    private static Map<String, Object> countTotalLine(List<Map<String, Object>> fileLineInfoList){
        int totalCodeSum = 0;
        int totalBlankSum = 0;
        int totalCommentSum = 0;

        for(Map<String, Object> fileLineInfo : fileLineInfoList){
            totalCodeSum += (int)fileLineInfo.get("codeSum");
            totalBlankSum += (int) fileLineInfo.get("blankSum");
            totalCommentSum += (int) fileLineInfo.get("commentSum");
        }

        Map<String, Object> map = new HashMap<>();
        map.put("totalCodeSum", totalCodeSum);
        map.put("totalBlankSum", totalBlankSum);
        map.put("totalCommentSum", totalCommentSum);

        return map;
    }

    private static void print(List<Map<String, Object>> fileLineInfoList){

        System.out.println("-----------------------------------------------------------");
        System.out.printf("|%20s |%10s |%10s |%10s |%n", "fileName", "code", "blank", "  comment");
        System.out.println("-----------------------------------------------------------");

        for(Map<String, Object> fileLineInfo : fileLineInfoList){
            if(fileLineInfo.containsKey("totalCodeSum")){
                int totalCodeSum = (int)fileLineInfo.get("totalCodeSum");
                int totalBlankSum = (int) fileLineInfo.get("totalBlankSum");
                int totalCommentSum = (int)fileLineInfo.get("totalCommentSum");
                System.out.println("-----------------------------------------------------------");
                System.out.printf("|%18s |%10d |%10d |%10d |%n", "합계", totalCodeSum
                        , totalBlankSum, totalCommentSum);
                System.out.println("-----------------------------------------------------------");
                System.out.printf("|%16s | %33d |%n", "총 라인 수", totalCodeSum+totalBlankSum+totalCommentSum);
                break;
            }
            System.out.printf("|%20s |%10d |%10d |%10d |%n", fileLineInfo.get("fileName"), (int)fileLineInfo.get("codeSum")
            , (int)fileLineInfo.get("blankSum"), (int)fileLineInfo.get("commentSum"));
        }

        System.out.println("-----------------------------------------------------------");
    }

    private static void printInfo(int infoNumber){
        switch (infoNumber){
            case INVALID_INPUT:
                System.out.println("올바르지 않은 입력입니다. 다시 입력해주세요"); break;
            case NOT_EXISTING_RESOURCE:
                System.out.println("파일 또는 폴더가 존재하지 않습니다. 다시 입력해주세요."); break;
            case EMPTY_DIRECTORY:
                System.out.println("폴더 내 파일이 존재하지 않습니다. 다시 입력해주세요."); break;
            default:
                System.out.println("유효하지 않은 입력입니다. 다시 입력해주세요.");
        }

        System.out.println("ex) LineCounter 파일or폴더명(절대경로) [(폴더일 때 제외할 파일)정규표현식]");
    }
}
