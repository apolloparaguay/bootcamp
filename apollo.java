package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "apollo", group = "TeleOp")
public class BotHex extends LinearOpMode {

    private DcMotor corehex;    
    private DcMotor hexmotor1;  
    private DcMotor hexmotor2; 
    private Servo   servo;     

    static final double FACTOR_VELOCIDAD = 0.5;   
    static final double FACTOR_LENTO     = 0.25;  
    static final double PASO_RUEDAS      = 0.04; 

    static final double POTENCIA_BRAZO = 0.4;   
    static final int    PASO_BRAZO     = 6;   
    static final int    BRAZO_MIN      = 0;    
    static final int    BRAZO_MAX      = 1200;  


    static final double PASO_GARRA  = 0.02;  
    static final double GARRA_MIN   = 0.0;   
    static final double GARRA_MAX   = 1.0;  


    double potIzqActual = 0;
    double potDerActual = 0;

  
    int objetivoBrazo = 0;

   
    double posGarra = 0;

    @Override
    public void runOpMode() {

    
        corehex   = hardwareMap.get(DcMotor.class, "corehex");
        hexmotor1 = hardwareMap.get(DcMotor.class, "hexmotor1");
        hexmotor2 = hardwareMap.get(DcMotor.class, "hexmotor2");
        servo     = hardwareMap.get(Servo.class,   "servo");

        hexmotor2.setDirection(DcMotor.Direction.REVERSE);

        hexmotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        hexmotor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    
        corehex.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        corehex.setTargetPosition(0);
        corehex.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        corehex.setPower(POTENCIA_BRAZO);

        telemetry.addLine("Listo. Brazo abajo? Presioná START");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

        
            double factor = gamepad1.left_bumper ? FACTOR_LENTO : FACTOR_VELOCIDAD;

            double potencia = -gamepad1.left_stick_y;
            double giro     =  gamepad1.right_stick_x;

            double objetivoIzq = Range.clip((potencia + giro) * factor, -1.0, 1.0);
            double objetivoDer = Range.clip((potencia - giro) * factor, -1.0, 1.0);

            potIzqActual = rampa(objetivoIzq, potIzqActual, PASO_RUEDAS);
            potDerActual = rampa(objetivoDer, potDerActual, PASO_RUEDAS);

            hexmotor1.setPower(potIzqActual);
            hexmotor2.setPower(potDerActual);

        
            if (gamepad1.dpad_up) {
                objetivoBrazo += PASO_BRAZO;
            } else if (gamepad1.dpad_down) {
                objetivoBrazo -= PASO_BRAZO;
            }
         
            objetivoBrazo = Range.clip(objetivoBrazo, BRAZO_MIN, BRAZO_MAX);

            corehex.setTargetPosition(objetivoBrazo);
            corehex.setPower(POTENCIA_BRAZO);

      
            if (gamepad1.a) {
                posGarra += PASO_GARRA;   // abrir
            } else if (gamepad1.b) {
                posGarra -= PASO_GARRA;   // cerrar
            }
            posGarra = Range.clip(posGarra, GARRA_MIN, GARRA_MAX);
            servo.setPosition(posGarra);

            // ===== Telemetría =====
            telemetry.addData("Modo", gamepad1.left_bumper ? "LENTO" : "Normal");
            telemetry.addData("Brazo objetivo", objetivoBrazo);
            telemetry.addData("Brazo real", corehex.getCurrentPosition());
            telemetry.addData("Garra", posGarra);
            telemetry.update();
        }
    }


    private double rampa(double objetivo, double actual, double paso) {
        if (objetivo > actual) {
            return Math.min(actual + paso, objetivo);
        } else if (objetivo < actual) {
            return Math.max(actual - paso, objetivo);
        }
        return objetivo;
    }
}