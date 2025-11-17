package PetCareScheduler;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PetCareScheduler {

    private static ArrayList<Pet> pets = new ArrayList<>();
    private static ArrayList<Appointment> appointments = new ArrayList<>();

    private static final String PET_FILE = "pets.txt";
    private static final String APPOINTMENT_FILE = "appointments.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        loadPetData();
        loadAppointmentData();

        boolean running = true;

        while (running) {
            System.out.println("\n===== Pet Care Scheduler =====");
            System.out.println("1. Register New Pet");
            System.out.println("2. Schedule Appointment");
            System.out.println("--- Display Data ---");
            System.out.println("3. Display All Pets");
            System.out.println("4. Display All Appointments (Global)");
            System.out.println("5. Display Appointments for Specific Pet");
            System.out.println("6. Display Upcoming Appointments");
            System.out.println("7. Display Past Appointments");
            System.out.println("--- Reports & Save ---");
            System.out.println("8. Generate Overdue Reports");
            System.out.println("9. Save Data");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        registerPet(scanner);
                        break;
                    case 2:
                        scheduleAppointment(scanner);
                        break;
                    case 3:
                        displayPets();
                        break;
                    case 4:
                        displayAppointments();
                        break;
                    case 5:
                        displayAppointmentsForPet(scanner);
                        break;
                    case 6:
                        displayUpcomingAppointments();
                        break;
                    case 7:
                        displayPastAppointments();
                        break;
                    case 8:
                        generateReports(scanner);
                        break;
                    case 9:
                        savePetData();
                        saveAppointmentData();
                        System.out.println("Data saved successfully!");
                        break;
                    case 0:
                        running = false;
                        System.out.println("Exiting... Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: Invalid input! Please enter a number from the menu.");
            }
        }

        scanner.close();
    }

    // ==============================================================================
    // ========== Pet Registration (REVISED for Full Validation) ==========
    // ==============================================================================
    private static void registerPet(Scanner scanner) {
        System.out.println("\n=== Register New Pet ===");
        int id = -1;
        boolean idValid = false;

        // 1. Validasi dan cek keunikan Pet ID
        while (!idValid) {
            System.out.print("Pet ID (integer): ");
            try {
                id = Integer.parseInt(scanner.nextLine());

                // Cek ID harus bilangan positif
                if (id <= 0) {
                    System.out.println("Error: Pet ID must be a positive integer.");
                    continue;
                }

                // Cek ID duplikat
                if (findPetById(id) != null) {
                    System.out.println("Error: Pet ID already exists! Please choose a different ID.");
                } else {
                    idValid = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid ID format. Please enter an integer.");
            }
        }

        // 2. Baca dan validasi input string (memastikan tidak kosong dan di-trim)
        String name = readValidString(scanner, "Pet Name: ", "Pet Name");
        String breed = readValidString(scanner, "Species/Breed: ", "Species/Breed");

        // 3. Baca dan validasi Usia (menggunakan metode bantu)
        int age = readValidAge(scanner);

        String owner = readValidString(scanner, "Owner Name: ", "Owner Name");
        String contact = readValidString(scanner, "Contact Info (Phone or Email): ", "Contact Info");

        // 4. Buat objek Pet. Tanggal registrasi dan list appointment diinisialisasi
        // dalam konstruktor Pet.
        Pet pet = new Pet(id, name, breed, age, owner, contact);
        pets.add(pet);

        System.out.println("Pet registered successfully!");
        System.out.println("Reminder: Select option 9 to save data permanently.");
    }

    /**
     * Helper method to read string input and ensure it is not empty.
     */
    private static String readValidString(Scanner scanner, String prompt, String fieldName) {
        String input = "";
        while (input.trim().isEmpty()) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Error: " + fieldName + " cannot be empty. Please try again.");
            }
        }
        return input;
    }

    /**
     * Helper method to read age input and ensure it is a valid integer within a
     * reasonable range (0-30).
     */
    private static int readValidAge(Scanner scanner) {
        int age = -1;
        final int MAX_AGE = 30;

        while (age < 0 || age > MAX_AGE) {
            System.out.print("Age (years, 0-" + MAX_AGE + "): ");
            try {
                age = Integer.parseInt(scanner.nextLine());
                if (age < 0 || age > MAX_AGE) {
                    System.out.println("Error: Age must be between 0 and " + MAX_AGE + " years. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid age format. Please enter a whole number.");
                age = -1; // Reset age to stay in loop
            }
        }
        return age;
    }
    // ==============================================================================
    // ==============================================================================

    // ========== Appointment Scheduling ==========
    private static void scheduleAppointment(Scanner scanner) {
        try {
            System.out.println("\n=== Schedule Appointment ===");

            System.out.print("Pet ID: ");
            int petId = Integer.parseInt(scanner.nextLine());

            Pet pet = findPetById(petId);
            if (pet == null) {
                System.out.println("Error: Pet not found!");
                return;
            }

            System.out.print("Appointment Type (vet, grooming, vaccination): ");
            String type = scanner.nextLine().toLowerCase();

            if (!type.equals("vet") && !type.equals("grooming") && !type.equals("vaccination")) {
                System.out.println("Error: Invalid appointment type!");
                return;
            }

            System.out.print("Date (yyyy-MM-dd): ");
            String dateInput = scanner.nextLine();

            System.out.print("Time (HH:mm): ");
            String timeInput = scanner.nextLine();

            LocalDateTime dateTime = LocalDateTime.parse(
                    dateInput + " " + timeInput,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            if (dateTime.isBefore(LocalDateTime.now())) {
                System.out.println("Error: Appointment must be scheduled for a future date/time!");
                return;
            }

            System.out.print("Notes (optional): ");
            String notes = scanner.nextLine();

            Appointment appointment = new Appointment(type, dateTime, notes);

            appointments.add(appointment);
            pet.addAppointment(appointment);

            System.out.println("Appointment scheduled successfully!");

        } catch (Exception e) {
            System.out.println("Error scheduling appointment: " + e.getMessage());
        }
    }

    private static Pet findPetById(int id) {
        for (Pet p : pets) {
            if (p.getPetId() == id)
                return p;
        }
        return null;
    }

    // ========== Display Pets ==========
    private static void displayPets() {
        System.out.println("\n=== All Registered Pets ===");

        if (pets.isEmpty()) {
            System.out.println("No pets found.");
            return;
        }

        for (Pet pet : pets) {
            System.out.println(pet);
            System.out.println("------------------------------");
        }
    }

    // ========== Display Appointments ==========
    private static void displayAppointments() {
        System.out.println("\n=== All Appointments ===");

        if (appointments.isEmpty()) {
            System.out.println("No appointments found.");
            return;
        }

        for (Appointment app : appointments) {
            System.out.println(app);
            System.out.println("------------------------------");
        }
    }

    // ========== Display Appointments for Specific Pet (Uses List) ==========
    private static void displayAppointmentsForPet(Scanner scanner) {
        System.out.println("\n=== Appointments for a Specific Pet ===");
        System.out.print("Enter Pet ID: ");
        int petId;

        try {
            petId = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("Error: Invalid Pet ID format.");
            return;
        }

        Pet pet = findPetById(petId);
        if (pet == null) {
            System.out.println("Error: Pet not found!");
            return;
        }

        System.out.println("\n--- Displaying appointments for: " + pet.getName() + " (ID: " + pet.getPetId() + ") ---");

        // Menggunakan List sebagai tipe data kembalian (konsisten dengan Pet.java baru)
        List<Appointment> petAppointments = pet.getAppointments();

        if (petAppointments.isEmpty()) {
            System.out.println("This pet has no scheduled appointments.");
            return;
        }

        for (Appointment app : petAppointments) {
            System.out.println(app);
            System.out.println("------------------------------");
        }
    }

    // ========== Display Upcoming Appointments ==========
    private static void displayUpcomingAppointments() {
        System.out.println("\n=== Upcoming Appointments (All Pets) ===");
        LocalDateTime now = LocalDateTime.now();
        boolean found = false;

        // Menggunakan daftar 'appointments' global
        for (Appointment app : appointments) {
            // Cek apakah janji temu terjadi 'setelah' waktu sekarang
            if (app.getDateTime().isAfter(now)) {
                System.out.println(app);
                System.out.println("------------------------------");
                found = true;
            }
        }

        if (!found) {
            System.out.println("No upcoming appointments found.");
        }
    }

    // ========== Display Past Appointments (Uses List) ==========
    private static void displayPastAppointments() {
        System.out.println("\n=== Past Appointment History (By Pet) ===");
        LocalDateTime now = LocalDateTime.now();

        if (pets.isEmpty()) {
            System.out.println("No pets registered to check history.");
            return;
        }

        // Iterasi melalui setiap hewan peliharaan
        for (Pet pet : pets) {
            System.out.println("\n--- History for " + pet.getName() + " (ID: " + pet.getPetId() + ") ---");

            // Menggunakan List sebagai tipe data kembalian (konsisten dengan Pet.java baru)
            List<Appointment> petAppointments = pet.getAppointments();
            boolean found = false;

            for (Appointment app : petAppointments) {
                // Cek apakah janji temu terjadi 'sebelum' waktu sekarang
                if (app.getDateTime().isBefore(now)) {
                    System.out.println(app);
                    System.out.println("------------------------------");
                    found = true;
                }
            }

            if (!found) {
                System.out.println("No past appointment history found for this pet.");
            }
        }
    }

    // ========== Reports ==========
    // ========== Reports Menu (Revised to accept user input) ==========
    private static void generateReports(Scanner scanner) {
        boolean reporting = true;
        while (reporting) {
            System.out.println("\n=== Generate Reports ===");
            System.out.println("1. Upcoming Appointments (Next 7 Days)");
            System.out.println("2. Pets Overdue for Vet Visit (6 Months)");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose report option: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        reportUpcomingAppointments();
                        break;
                    case 2:
                        reportOverdueVetVisits();
                        break;
                    case 0:
                        reporting = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid input. Please enter a number.");
            }
        }
    }

    // ========== Report 1: Upcoming Appointments (Extracted and Hardened)
    // ==========
    private static void reportUpcomingAppointments() {
        System.out.println("\n=== Upcoming Appointments (Next 7 Days) ===");

        if (appointments.isEmpty()) {
            System.out.println("No appointments found in the system.");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        boolean found = false;

        for (Appointment a : appointments) {
            // Cek janji temu yang berada di masa depan DAN dalam 7 hari ke depan
            if (a.getDateTime().isAfter(now) &&
                    a.getDateTime().isBefore(now.plusDays(7))) {

                System.out.println(a);
                System.out.println("------------------------------");
                found = true;
            }
        }

        if (!found) {
            System.out.println("No upcoming appointments found in the next 7 days.");
        }
    }

    // ========== Report 2: Pets Overdue for Vet Visit (Extracted and Hardened)
    // ==========
    private static void reportOverdueVetVisits() {
        System.out.println("\n=== Pets Overdue for Vet Visit (6 Months) ===");

        if (pets.isEmpty()) {
            System.out.println("No pets registered in the system.");
            return;
        }

        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        boolean overdueFound = false;

        for (Pet p : pets) {
            boolean hasRecentVet = false;

            // Menggunakan List untuk konsistensi dengan Pet.java yang sudah direvisi
            List<Appointment> petAppointments = p.getAppointments();

            for (Appointment a : petAppointments) {
                // Robustness: Menggunakan equalsIgnoreCase untuk perbandingan tipe janji temu
                // Cek apakah kunjungan dokter hewan terjadi 6 bulan yang lalu atau lebih baru
                if (a.getAppointmentType().equalsIgnoreCase("vet") &&
                        (a.getDateTime().isEqual(sixMonthsAgo) || a.getDateTime().isAfter(sixMonthsAgo))) {

                    hasRecentVet = true;
                    break;
                }
            }

            if (!hasRecentVet) {
                System.out.println("Pet: " + p.getName() + " (ID: " + p.getPetId() + ") - NEEDS VET ATTENTION!");
                overdueFound = true;
            }
        }

        if (!overdueFound) {
            System.out.println(
                    "No pets found overdue for a vet visit (all have had a vet visit within the last 6 months).");
        }
    }

    // ========== File I/O : SAVE ==========
    // ========== File I/O : SAVE PETS (REVISED) ==========
    private static void savePetData() {
        // 1. Add null/empty guard
        if (pets == null || pets.isEmpty()) {
            System.out.println("Warning: No pets to save. Skipping pet data saving.");
            return;
        }

        // 2. Catch specific IOException
        try (PrintWriter writer = new PrintWriter(new FileWriter(PET_FILE))) {

            for (Pet p : pets) {
                writer.println(
                        p.getPetId() + "|" +
                                p.getName() + "|" +
                                p.getSpeciesOrBreed() + "|" +
                                p.getAge() + "|" +
                                p.getOwnerName() + "|" +
                                p.getContactInfo() + "|" +
                                p.getRegistrationDate());
            }

        } catch (IOException e) {
            System.out.println("Error saving pets: " + e.getMessage());
        }
    }

    // ========== File I/O : SAVE APPOINTMENTS (REVISED for Pet ID Association)
    // ==========
    private static void saveAppointmentData() {
        // 1. Add null/empty guard
        if (pets == null || pets.isEmpty()) {
            // Jika tidak ada pets, tidak ada appointments yang perlu disimpan.
            System.out.println("Warning: No pets available. Skipping appointment data saving.");
            return;
        }

        // 2. Catch specific IOException
        try (PrintWriter writer = new PrintWriter(new FileWriter(APPOINTMENT_FILE))) {

            // ITERASI melalui PETS untuk mendapatkan asosiasi ID
            for (Pet p : pets) {
                // Menggunakan getAppointments() dari Pet yang mengembalikan List yang valid
                // untuk iterasi
                for (Appointment a : p.getAppointments()) {
                    writer.println(
                            // 3. EXPLICIT ASSOCIATION: Menyimpan Pet ID
                            p.getPetId() + "|" +
                                    a.getAppointmentType() + "|" +
                                    a.getDateTime() + "|" +
                                    a.getNotes());
                }
            }

        } catch (IOException e) {
            System.out.println("Error saving appointments: " + e.getMessage());
        }
    }

    // ========== File I/O : LOAD (REVISED for registrationDate) ==========
    private static void loadPetData() {
        File file = new File(PET_FILE);
        if (!file.exists()) {
            // System.out.println("Info: Pet file not found. Starting with empty list.");
            return;
        }

        int loadedCount = 0;
        int lineCounter = 0;
        final int EXPECTED_FIELDS = 7; // petId, name, breed, age, owner, contact, registrationDate

        try (Scanner sc = new Scanner(file)) {

            System.out.println("Loading pet data...");
            while (sc.hasNextLine()) {
                lineCounter++;
                String line = sc.nextLine();
                String[] data = line.split("\\|");

                // 1. Validasi Jumlah Field
                if (data.length != EXPECTED_FIELDS) {
                    System.out.println("Load Error (Pets) Line " + lineCounter + ": Skipping malformed line. Expected "
                            + EXPECTED_FIELDS + " fields, got " + data.length + ".");
                    continue;
                }

                try {
                    // 2. Parsed Data (dengan trim)
                    int id = Integer.parseInt(data[0].trim());
                    String name = data[1].trim();
                    String breed = data[2].trim();
                    int age = Integer.parseInt(data[3].trim());
                    String owner = data[4].trim();
                    String contact = data[5].trim();

                    Pet p = new Pet(id, name, breed, age, owner, contact);
                    pets.add(p);
                    loadedCount++;

                } catch (NumberFormatException e) {
                    System.out.println("Load Error (Pets) Line " + lineCounter + ": Invalid number format (ID or Age). "
                            + e.getMessage());
                } catch (DateTimeParseException e) {
                    System.out.println("Load Error (Pets) Line " + lineCounter
                            + ": Invalid date format for Registration Date. " + e.getMessage());
                }
            }

        } catch (FileNotFoundException e) {
            // Ini seharusnya tidak terjadi karena sudah dicek 'file.exists()', tetapi tetap
            // ditambahkan untuk kelengkapan.
            System.out.println("Load Error (Pets): File not found.");
        } catch (Exception e) {
            System.out.println("Load Error (Pets) - Unhandled Exception: " + e.getMessage());
        }

        // 4. Print Ringkasan
        System.out.println("Loaded " + loadedCount + " pet records successfully.");
    }

    // ========== File I/O : LOAD APPOINTMENTS (REVISED for Robustness and Pet ID
    // link) ==========
    private static void loadAppointmentData() {
        File file = new File(APPOINTMENT_FILE);
        if (!file.exists()) {
            // System.out.println("Info: Appointment file not found. Starting with empty
            // list.");
            return;
        }

        int loadedCount = 0;
        int lineCounter = 0;
        final int MIN_EXPECTED_FIELDS = 3; // petId, type, dateTime, (optional) notes

        try (Scanner sc = new Scanner(file)) {

            System.out.println("Loading appointment data...");
            while (sc.hasNextLine()) {
                lineCounter++;
                String line = sc.nextLine();
                String[] data = line.split("\\|");

                // 1. Validasi Jumlah Field
                if (data.length < MIN_EXPECTED_FIELDS) {
                    System.out.println("Load Error (Appointments) Line " + lineCounter
                            + ": Skipping malformed line. Expected at least " + MIN_EXPECTED_FIELDS + " fields, got "
                            + data.length + ".");
                    continue;
                }

                try {
                    // 2. Parsed Data (dengan trim)
                    int petId = Integer.parseInt(data[0].trim());
                    String type = data[1].trim();
                    LocalDateTime dateTime = LocalDateTime.parse(data[2].trim());

                    // Defensively handle optional Notes field
                    String notes = data.length > 3 ? data[3].trim() : "";

                    Appointment a = new Appointment(type, dateTime, notes);
                    appointments.add(a);
                    loadedCount++;

                    // Menghubungkan Appointment kembali ke objek Pet yang benar
                    Pet pet = findPetById(petId);
                    if (pet != null) {
                        pet.addAppointment(a);
                    } else {
                        System.out.println("Load Warning (Appointments) Line " + lineCounter
                                + ": Appointment loaded, but Pet ID " + petId + " not found to link.");
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Load Error (Appointments) Line " + lineCounter
                            + ": Invalid number format for Pet ID. " + e.getMessage());
                } catch (DateTimeParseException e) {
                    System.out.println("Load Error (Appointments) Line " + lineCounter + ": Invalid date/time format. "
                            + e.getMessage());
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("Load Error (Appointments): File not found.");
        } catch (Exception e) {
            System.out.println("Load Error (Appointments) - Unhandled Exception: " + e.getMessage());
        }

        // 4. Print Ringkasan
        System.out.println("Loaded " + loadedCount + " appointment records successfully.");
    }
}