import com.quantumSamurais.hams.database.Database;
public class FutureAppView {
   


    public boolean bookApp(Doctor doctor, Appointment app, Patient pat){
        try{
            doctor.acceptAppointment(app);
            String id = app.getAppointmentID().toString();
            pat.appointments.put(id, app);
            return true;

        } catch{
            return false

        }
    }




}
