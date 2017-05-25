package fr.esialrobotik.data.table;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ForbiddenAreaChecker {
    private Table table;
    private JTextField response;

    public ForbiddenAreaChecker() throws IOException {
        table = new Table();
        table.loadFromSaveFile("pathFinding/table.tbl");

        response = new JTextField(10);
        response.setEnabled(false);

        response.setOpaque(true);

        JFrame frame = new JFrame("Checker");
        JPanel panel = new JPanel();
        JButton check = new JButton("Check");
        SpinnerNumberModel xModel = new SpinnerNumberModel(0, 0, 10000, 10);
        SpinnerNumberModel yModel = new SpinnerNumberModel(0, 0, 10000, 10);

        JTextField textField = new JTextField(10);
        panel.add(textField);

        JButton load = new JButton("Load");
        load.addActionListener(e -> {
            String[] arg = textField.getText().split(",");
            if(arg.length == 2) {
                int x = Integer.parseInt(arg[0].trim());
                int y = Integer.parseInt(arg[1].trim());
                xModel.setValue(x);
                yModel.setValue(y);
                check.doClick();
            }
        });
        panel.add(load);

        JSpinner xSpinner = new JSpinner(xModel);
        panel.add(xSpinner);


        JSpinner ySpinner = new JSpinner(yModel);
        panel.add(ySpinner);


        check.addActionListener(e -> checkCoordinate(xModel.getNumber().intValue(), yModel.getNumber().intValue()));
        panel.add(check);

        panel.add(response);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    private void checkCoordinate(int x, int y) {
        System.out.println("here " + x + " " + y + " " + this.table.isAreaForbiddenSafe(x / 10, y / 10));
        if(this.table.isAreaForbiddenSafe(x / 10, y / 10)) {
            this.response.setBackground(Color.RED);
        }
        else {
            this.response.setBackground(Color.GREEN);
        }
    }


    public static void main(String args[]) throws IOException {
        new ForbiddenAreaChecker();
    }
}