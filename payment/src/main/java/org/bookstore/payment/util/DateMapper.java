package org.bookstore.payment.util;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;

public class DateMapper {

    public static LocalDateTime map(XMLGregorianCalendar calendar) {
        try {
            return new java.sql.Timestamp(
                calendar.toGregorianCalendar().getTime().getTime()).toLocalDateTime();
        } catch (Exception ex) {
            return null;
        }
    }

}
