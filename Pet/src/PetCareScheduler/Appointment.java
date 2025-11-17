package PetCareScheduler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Appointment {

    private String appointmentType; // vet visit, grooming, vaccination
    private LocalDateTime dateTime; // Date & time of appointment
    private String notes; // Optional

    // Constructor
    public Appointment(String appointmentType, LocalDateTime dateTime, String notes) {
        this.appointmentType = appointmentType;
        this.dateTime = dateTime;
        this.notes = notes;
    }

    // Getters
    public String getAppointmentType() {
        return appointmentType;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getNotes() {
        return notes;
    }

    // Setters
    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Override toString for readable output
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        String output = "Appointment Type: " + appointmentType +
                "\nDate & Time: " + dateTime.format(formatter);

        if (notes != null && !notes.trim().isEmpty()) {
            output += "\nNotes: " + notes;
        }

        return output;
    }
}