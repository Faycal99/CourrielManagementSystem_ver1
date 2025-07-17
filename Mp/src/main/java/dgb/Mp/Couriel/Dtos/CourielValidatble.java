package dgb.Mp.Couriel.Dtos;

import dgb.Mp.Couriel.enums.Couriel_Type;
import dgb.Mp.Couriel.enums.Nature;
import dgb.Mp.Couriel.enums.Priority;
import dgb.Mp.Couriel.enums.Status;
import dgb.Mp.Utils.AlgerianMinistries;

import java.time.LocalDate;

public interface CourielValidatble {
    String getCourielNumber();
    String getType();
    String getNature();
    Status getStatus();
    String getSubject();
    Priority getPriority();
    LocalDate getArrivedDate();
    LocalDate getSentDate();
    LocalDate getReturnDate();
  //  LocalDate getSavedDate();
    Long getFromDivisionId();
    Long getFromDirectionId();
    Long getFromSousDirectionId();
    AlgerianMinistries getFromExternal();
    Long getToDivisionId();
    Long getToDirectionId();
    Long getToSousDirectionId();
    AlgerianMinistries getToExternal();
    String getDescription();
}