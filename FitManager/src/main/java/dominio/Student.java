package dominio;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class Student {
    private String name;
    private String cpf;
    private String contact;
    private LocalDate birthDate;
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private boolean active;

    public Student(String name, String cpf, String contact, LocalDate birthDate, boolean active) {
        this.name = name;
        this.cpf = cpf;
        this.contact = contact;
        this.birthDate = birthDate;
        this.active = active;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    
    public void activate(){
        this.active = true;
    }
    
    public void deactivate(){
        this.active = false;
    }
    
    public String getName() {
        return name;
    }

    public String getCpf() {
        return cpf;
    }

    public String getContact() {
        return contact;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public boolean isActive() {
        return active;
    }
    
    public static boolean validateCpf(String cpf){
        /*tratamento em regex para tirar pontos e traços*/
        cpf = cpf.replaceAll("\\D", "");

        /*se tiver mais que 11 dígitos devolve false*/
        if (cpf.length() != 11)
            return false;

        /*verificação em regex para olhar cpf do tipo "11111111111" e devolve false*/
        if (cpf.matches("(\\d)\\1{10}"))
            return false;
        
        /*declaração*/
        int i = 0, multiplier = 10, sum = 0, verificationDigit1 = -1, verificationDigit2 = -1;
        
        while(i<9){
            char c = cpf.charAt(i);
            
            sum = sum + ((c - '0') * multiplier);
            multiplier = multiplier-1;
            
            i = i+1;
        }
        
        verificationDigit1 = 11-(sum%11);
        if(verificationDigit1 >= 10)
            verificationDigit1 = 0;
                
        if(cpf.charAt(9) != (verificationDigit1 + '0'))
            return false;
        
        i = 0;
        sum = 0;
        multiplier = 11;
        while(i < 10){
            char c = cpf.charAt(i);
            
            sum = sum + ((c - '0') * multiplier);
            multiplier = multiplier-1;
            i = i+1;
        }
        
        verificationDigit2 = 11-(sum%11);
        if(verificationDigit2 >= 10)
            verificationDigit2 = 0;
        
        if(cpf.charAt(10) != (verificationDigit2 + '0'))
            return false;
        
        return true;
    }
    
    public int calculateAge(){
        LocalDate today = LocalDate.now();
        Period entireAge = Period.between(birthDate, today);
        
        return entireAge.getYears();
    }
}
