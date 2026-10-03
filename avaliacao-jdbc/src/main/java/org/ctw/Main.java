package org.ctw;

import org.ctw.config.ConnectionFactory;
import org.ctw.dao.AlertaMotorDAO;
import org.ctw.dao.MotorDAO;
import org.ctw.dao.SetorDAO;
import org.ctw.dao.TelemetriaDAO;
import org.ctw.service.AlertaMotorService;
import org.ctw.service.MotorService;
import org.ctw.service.SetorService;
import org.ctw.service.TelemetriaService;
import org.ctw.view.MenuConsole;

import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // DAOs
        SetorDAO setorDAO = new SetorDAO();
        MotorDAO motorDAO = new MotorDAO();
        TelemetriaDAO telemetriaDAO =
                new TelemetriaDAO();
        AlertaMotorDAO alertaDAO =
                new AlertaMotorDAO();

        // Services
        SetorService setorService =
                new SetorService(setorDAO);

        MotorService motorService =
                new MotorService(motorDAO);

        TelemetriaService telemetriaService =
                new TelemetriaService(
                        telemetriaDAO,
                        motorService
                );

        AlertaMotorService alertaService =
                new AlertaMotorService(
                        alertaDAO,
                        motorService
                );

        // View
        try (Scanner scanner = new Scanner(System.in)) {

            MenuConsole menu =
                    new MenuConsole(
                            scanner,
                            setorService,
                            motorService,
                            telemetriaService,
                            alertaService
                    );

            menu.iniciar();
        }
    }
}