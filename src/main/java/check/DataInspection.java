package check;

import com.vdurmont.emoji.EmojiParser;
import dto.NineToTwSevDto;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Date;

public class DataInspection {

    public void inspectionStart(Connection connection){
        try {

            Statement stmt = connection.createStatement();
            //SELECT 문을 사용할때 WHERE NOT 조건을 사용하여 WRITE_ACCOUNT(작성자계정)과 CONTACT(연락처) 둘다 없는 로우를 가져오지 않는다.
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM test.test WHERE NOT (writer_account is null AND contact is null)");

            while (resultSet.next()){
                NineToTwSevDto nineToTwSevDto = new NineToTwSevDto();
                if(checkUrl(resultSet.getString("url"))){
                    String channel = resultSet.getString("channel");
                    String title = changeData(textLimit(emoji(resultSet.getString("title"))));
                    String date=changeDate(resultSet.getString("write_date"));
                    String time =changeTime(resultSet.getString("write_time"));
                    String writerName = nullCheck(resultSet.getString("writer_name"));
                    String writerAccount = nullCheck(resultSet.getString("writer_account"));
                    String contact = nullCheck(resultSet.getString("contact"));
                    Array arrayList = resultSet.getArray("keyword_groups");
                    nineToTwSevDto.setUrl(resultSet.getString("url"));
                    nineToTwSevDto.setChanneel(channel);
                    nineToTwSevDto.setTitle(title);
                    nineToTwSevDto.setWriteDate(date);
                    nineToTwSevDto.setWriteTime(time);
                    nineToTwSevDto.setWriteName(writerName);
                    nineToTwSevDto.setWriteAccount(writerAccount);
                    nineToTwSevDto.setContact(contact);
                    System.out.println(nineToTwSevDto.toString());

                }else{
                }
            }
        }catch (SQLException e){
            System.out.println(e);
        }

    }

    //url 정규식을 확인
    public Boolean checkUrl(String url){
        Pattern p = Pattern.compile("^(?:https?:\\/\\/)?(?:www\\.)?[a-zA-Z0-9./]+$");
        Matcher m = p.matcher(url);
        if  (m.matches()){
            return true;
        }
        URL u = null;
        try {
            u = new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }
        try {
            u.toURI();
        } catch (URISyntaxException e) {
            return false;
        }
        return true;
    }
// data 내부에있는 이모티콘을 제거
    public String emoji(String data){
        String removeEmoji = EmojiParser.removeAllEmojis(data);
        return removeEmoji;
    }
    //글길이를 60자로 제한
    public String textLimit(String text){
        if(text.length() > 60){
           String limitText = text.substring(0,56);
           return limitText+"...";
        }
        return text;
    }
//db writeTime의 시간형태를 변형
    public String changeTime(String time){
        DateFormat timeParse = new SimpleDateFormat("HHmm");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String timeC=null;
        try {
            switch (time.length()) {
                case 1:
                    time = "000" + time;
                    break;
                case 2:
                    time = "00" + time;
                    break;
                case 3:
                    time = "00" + time;
                    break;
            }
        }catch (NullPointerException e){
//            System.out.println("널");
            return null;
        }
        try {
            Date timePar = timeParse.parse(time);
            timeC = timeFormat.format(timePar);
//            System.out.println(timeC);
        }
        catch (ParseException e){
            System.out.println(e);
        }finally {
            return timeC;
        }

    }
    //db WriteDate 형태 변환
    public String changeDate(String date){
        DateFormat dateParse = new SimpleDateFormat("yyyyMMdd");
        DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
        String dateC = null;

        try {
           Date datePar = dateParse.parse(date);
           dateC = dateFormat.format(datePar);
//            System.out.println(dateC);
        }
        catch (ParseException e){
            System.out.println(e);
        }finally {
            return dateC;
        }
    }

    public String changeData(String data){
       String replaceData = data.replace("\t"," ").replace("\r\n"," ").replace(",","^");
       return replaceData;
    }
    public String nullCheck(String data){
        try {
        }catch (NullPointerException e){
            System.out.println(e);
            return null;
        }
        return data;
    }
}
