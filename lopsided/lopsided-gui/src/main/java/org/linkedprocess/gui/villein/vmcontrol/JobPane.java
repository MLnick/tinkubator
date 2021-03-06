package org.linkedprocess.gui.villein.vmcontrol;

import org.jivesoftware.smack.packet.PacketExtension;
import org.linkedprocess.gui.GenericErrorHandler;
import org.linkedprocess.villein.Handler;
import org.linkedprocess.villein.proxies.JobProxy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * @version LoPSideD 0.1
 */
public class JobPane extends JPanel implements ActionListener {

    protected String jobId;
    protected JTextArea expressionTextArea;
    protected JTextArea resultTextArea;
    protected JButton submitJobButton;
    protected JButton clearButton;
    protected VmControlFrame vmControlFrame;

    protected static final String SUBMIT_JOB = "submit job";
    protected static final String CLEAR = "clear";
    protected static final String ABORT_JOB = "abort job";
    protected static final String BLANK_STRING = "";


    public JobPane(VmControlFrame vmControlFrame, String jobId) {
        super(new BorderLayout());
        this.setOpaque(false);
        this.vmControlFrame = vmControlFrame;
        this.expressionTextArea = new JTextArea();
        this.resultTextArea = new JTextArea();
        this.resultTextArea.setEditable(false);
        JScrollPane scrollPane1 = new JScrollPane(this.expressionTextArea);
        JScrollPane scrollPane2 = new JScrollPane(this.resultTextArea);

        this.submitJobButton = new JButton(SUBMIT_JOB);
        this.submitJobButton.addActionListener(this);
        this.clearButton = new JButton(CLEAR);
        this.clearButton.addActionListener(this);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        scrollPane1.setOpaque(false);
        scrollPane2.setOpaque(false);
        buttonPanel.add(this.submitJobButton);
        buttonPanel.add(this.clearButton);


        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.add(scrollPane1);
        splitPane.add(scrollPane2);
        splitPane.setDividerLocation(200);
        splitPane.setOpaque(false);
        this.add(splitPane, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);
        this.jobId = jobId;
    }

    public String getJobId() {
        return this.jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.expressionTextArea.setEnabled(enabled);
        this.resultTextArea.setEnabled(enabled);
        this.submitJobButton.setEnabled(enabled);
        this.clearButton.setEnabled(enabled);
    }

    public void clearAllText() {
        this.expressionTextArea.setText(BLANK_STRING);
        this.resultTextArea.setText(BLANK_STRING);
    }

    public JobProxy createSubmitJobStruct() {
        JobProxy jobProxy = new JobProxy();
        jobProxy.setExpression(this.expressionTextArea.getText());
        jobProxy.setJobId(this.jobId);
        return jobProxy;
    }

    public JobProxy createAbortJobStruct() {
        JobProxy jobProxy = new JobProxy();
        jobProxy.setJobId(this.jobId);
        return jobProxy;
    }

    public void handleIncomingSubmitJob(JobProxy jobProxy) {
        if (jobProxy.getLopError() == null) {
            this.resultTextArea.setText(jobProxy.getResult());
            this.submitJobButton.setEnabled(false);
        } else {
            StringBuffer errorMessage = new StringBuffer();
            if (jobProxy.getLopError().getType() != null) {
                errorMessage.append(jobProxy.getLopError().getType().toString().toLowerCase() + "\n");
            }
            if (jobProxy.getLopError().getCondition() != null) {
                errorMessage.append(jobProxy.getLopError().getCondition() + "\n");
            }
            if (jobProxy.getLopError().getExtensions() != null) {
                for (PacketExtension extension : jobProxy.getLopError().getExtensions()) {
                    errorMessage.append(extension.getElementName() + "\n");
                }
            }
            if (jobProxy.getLopError().getMessage() != null) {
                errorMessage.append(jobProxy.getLopError().getMessage());
            }
            this.resultTextArea.setText(errorMessage.toString().trim());
            this.submitJobButton.setEnabled(false);
        }
    }

    public String toString() {
        return this.jobId;
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getActionCommand().equals(SUBMIT_JOB)) {
            Handler<JobProxy> submitJobHandler = new Handler<JobProxy>() {
                public void handle(JobProxy jobStruct) {
                    vmControlFrame.handleIncomingSubmitJob(jobStruct);
                }
            };
            this.vmControlFrame.getVmProxy().submitJob(this.createSubmitJobStruct(), submitJobHandler, submitJobHandler);
            submitJobButton.setText(ABORT_JOB);
            submitJobButton.setActionCommand(ABORT_JOB);
            clearButton.setEnabled(false);
            expressionTextArea.setEditable(false);
        } else if (event.getActionCommand().equals(CLEAR)) {
            this.expressionTextArea.setText("");
        } else if (event.getActionCommand().equals(ABORT_JOB)) {
            Handler<String> resultHandler = new Handler<String>() {
                public void handle(String jobId) {
                    vmControlFrame.handleIncomingAbortJob(jobId);
                }
            };
            this.vmControlFrame.getVmProxy().abortJob(this.createAbortJobStruct(), resultHandler, new GenericErrorHandler());
            this.expressionTextArea.setEditable(false);
            this.submitJobButton.setEnabled(false);
            this.clearButton.setEnabled(false);
        }
    }
}
