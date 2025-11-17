package PetCareScheduler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections; // Digunakan untuk membuat daftar yang tidak dapat diubah
import java.util.List; // Digunakan untuk encapsulation yang lebih baik

public class Pet {

    // Pet ID dibuat 'final' dan tidak memiliki setter, menjadikannya immutable
    // (read-only)
    private final int petId;
    private String name; // Pet name
    private String speciesOrBreed; // Breed or species
    private int age; // Age in years
    private String ownerName; // Owner full name
    private String contactInfo; // Phone or email
    // Registration Date dibuat 'final' dan tidak memiliki setter, menjadikannya
    // immutable (read-only)
    private final LocalDate registrationDate;
    private final ArrayList<Appointment> appointments; // List of appointments

    // Constructor
    public Pet(int petId, String name, String speciesOrBreed, int age,
            String ownerName, String contactInfo) {
        this.petId = petId;
        this.name = name;
        this.speciesOrBreed = speciesOrBreed;
        this.age = age;
        this.ownerName = ownerName;
        this.contactInfo = contactInfo;
        this.registrationDate = LocalDate.now();
        this.appointments = new ArrayList<>(); // initialize list
    }

    // Getters
    public int getPetId() {
        return petId;
    }

    public String getName() {
        return name;
    }

    public String getSpeciesOrBreed() {
        return speciesOrBreed;
    }

    public int getAge() {
        return age;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    /**
     * Mengembalikan tampilan daftar janji temu yang tidak dapat diubah
     * (unmodifiable list)
     * untuk mencegah modifikasi eksternal.
     */
    public List<Appointment> getAppointments() {
        // Mengembalikan unmodifiable list untuk melindungi ArrayList internal
        return Collections.unmodifiableList(this.appointments);
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setSpeciesOrBreed(String speciesOrBreed) {
        this.speciesOrBreed = speciesOrBreed;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    // Add an appointment
    public void addAppointment(Appointment appointment) {
        this.appointments.add(appointment);
    }

    // Method untuk menghapus janji temu (untuk kelengkapan)
    public boolean removeAppointment(Appointment appointment) {
        return this.appointments.remove(appointment);
    }

    @Override
    public String toString() {
        return "Pet ID: " + petId +
                "\nName: " + name +
                "\nSpecies/Breed: " + speciesOrBreed +
                "\nAge: " + age +
                "\nOwner: " + ownerName +
                "\nContact: " + contactInfo +
                "\nRegistered: " + registrationDate +
                "\nTotal Appointments: " + appointments.size();
    }
}