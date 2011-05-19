package org.codehaus.mojo.mail;

/* 
 * Copyright 2011 Markus W Mahlberg 
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0

 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/* import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
*/
import java.io.File;
import java.util.List;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Arrays;
 
import javax.mail.Message;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.apache.maven.plugin.testing.AbstractMojoTestCase ;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import org.jvnet.mock_javamail.Mailbox;

public class MailMojoTest
    extends AbstractMojoTestCase
{
    protected void setUp() throws Exception {
        super.setUp();
    }

    MailMojo mojo;

    /**
    *   Tests the discovery and configuration of MailMojo
    *   @throws Exception
    */
    public void testEnvironment() throws Exception {
        File testPom = new File(getBasedir(), "src/test/resources/unit/basic-test/plugin-config.xml");
        mojo = (MailMojo) lookupMojo ("mail", testPom);

        assertNotNull( mojo );
        assertNotNull( mojo.getLog() );
        String smtphost = (String) getVariableValueFromObject(mojo,"smtphost");
        assertNotNull(smtphost);
        assertEquals("localhost", smtphost );

        assertNotNull("from");
        assertNotNull("subject");
        assertNotNull("body");

    }

    /**
    * Tries to send a mail to Mock Javamail
    * see (http://java.net/projects/mock-javamail/)
    * @throws Exception
    */
    public void testSendMail() throws Exception {

        File testPom = new File(getBasedir(), "src/test/resources/unit/basic-test/plugin-config.xml");
        mojo = (MailMojo) lookupMojo ("mail", testPom);
        assertNotNull(mojo); // Make sure mojo is alive
        mojo.getLog().info("NOTICE: The mails will be sent to a mock mailserver");
        mojo.execute();
    }

   /**
    * Tests whether the mails were sent
    * @throws Exception
    */ 
    public void testRecipientMails() throws Exception {

        File testPom = new File(getBasedir(), "src/test/resources/unit/basic-test/plugin-config.xml");
        mojo = (MailMojo) lookupMojo ("mail", testPom);
        assertNotNull(mojo); // Make sure mojo is alive

        List<Message> inbox = Mailbox.get("a@localhost");
        assertEquals(inbox.size(),1);

        inbox = Mailbox.get("b@localhost");
        assertEquals(inbox.size(),1);

/*
        inbox = Mailbox.get("e@localhost");
        assertEquals(inbox.size(),1);
       
        inbox = Mailbox.get("f@localhost");
        assertEquals(inbox.size(),1);
*/
    }

   /**
    * Tests whether the CC-Mails are correct 
    * @throws Exception
    */ 

    public void testCCMails() throws Exception {
        
        File testPom = new File(getBasedir(), "src/test/resources/unit/basic-test/plugin-config.xml");
        mojo = (MailMojo) lookupMojo ("mail", testPom);
        assertNotNull(mojo); // Make sure mojo is alive

        List<Message> inbox = Mailbox.get("c@localhost");
        assertEquals(inbox.size(),1);

        Message msg = inbox.get(0);        
        assertNotNull(msg);

        HashSet recipients = new HashSet ( Arrays.asList((InternetAddress[]) msg.getRecipients(Message.RecipientType.TO) ) );

        assertTrue(recipients.contains(new InternetAddress("a@localhost") ) );
        assertTrue(recipients.contains(new InternetAddress("b@localhost") ) );

        HashSet ccRecipients = new HashSet ( Arrays.asList((InternetAddress[]) msg.getRecipients(Message.RecipientType.CC) ) );

        assertTrue(ccRecipients.contains(new InternetAddress("c@localhost") ) );
        assertTrue(ccRecipients.contains(new InternetAddress("d@localhost") ) );


        HashSet bccRecipients = new HashSet ( Arrays.asList((InternetAddress[]) msg.getRecipients(Message.RecipientType.BCC) ) );

        assertFalse(ccRecipients.contains(new InternetAddress("e@localhost") ) );
        assertFalse(ccRecipients.contains(new InternetAddress("f@localhost") ) );
    }

    /**
    * Tests whether the BCC-Mails are correct 
    * @throws Exception
    */

    public void testBCCMails() throws Exception {

        File testPom = new File(getBasedir(), "src/test/resources/unit/basic-test/plugin-config.xml");
        mojo = (MailMojo) lookupMojo ("mail", testPom);
        assertNotNull(mojo); // Make sure mojo is alive

        List<Message> inbox = Mailbox.get("e@localhost");
        assertEquals(inbox.size(),1);

        Message msg = inbox.get(0);        
        assertNotNull(msg);

        HashSet ccrecipients = new HashSet ( Arrays.asList((InternetAddress[]) msg.getRecipients(Message.RecipientType.BCC) ) );

        assertTrue(ccrecipients.contains(new InternetAddress("e@localhost") ) );
        assertTrue(ccrecipients.contains(new InternetAddress("f@localhost") ) );
    }
}
