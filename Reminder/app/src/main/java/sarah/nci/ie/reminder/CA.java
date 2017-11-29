package sarah.nci.ie.reminder;

/**
 * Created by User on 11/20/2017.
 */

public class CA {
    private String Subject, CATitle, date;

    public CA(String subject, String CATitle, String date) {
        Subject = subject;
        this.CATitle = CATitle;
        this.date = date;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getCATitle() {
        return CATitle;
    }

    public void setCATitle(String CATitle) {
        this.CATitle = CATitle;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
