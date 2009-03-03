package org.apache.maven.it;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;

import java.io.File;
import java.util.List;

/**
 * This is a test set for <a href="http://jira.codehaus.org/browse/MNG-4056">MNG-4056</a>.
 * 
 * @author Benjamin Bentmann
 */
public class MavenITmng4056ClassifierBasedDepResolutionFromReactorTest
    extends AbstractMavenIntegrationTestCase
{

    public MavenITmng4056ClassifierBasedDepResolutionFromReactorTest()
    {
        super( "[2.1.0,)" );
    }

    /**
     * Test that attached artifacts can be resolved from the reactor cache even if the dependency declaration
     * in the consumer module does not use the proper artifact type but merely specifies the classifier.
     */
    public void testit()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-4056" );

        Verifier verifier = new Verifier( testDir.getAbsolutePath() );
        verifier.setAutoclean( false );
        verifier.deleteDirectory( "consumer/target" );
        verifier.deleteArtifacts( "org.apache.maven.its.mng4056" );
        verifier.executeGoal( "validate" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        List artifacts = verifier.loadLines( "consumer/target/artifacts.txt", "UTF-8" );
        assertTrue( artifacts.toString(), 
            artifacts.contains( "org.apache.maven.its.mng4056:producer:test-jar:tests:0.1" ) );
        assertTrue( artifacts.toString(), 
            artifacts.contains( "org.apache.maven.its.mng4056:producer:java-source:sources:0.1" ) );
        assertTrue( artifacts.toString(), 
            artifacts.contains( "org.apache.maven.its.mng4056:producer:javadoc:javadoc:0.1" ) );
        assertTrue( artifacts.toString(), 
            artifacts.contains( "org.apache.maven.its.mng4056:producer:ejb-client:client:0.1" ) );

        List classpath = verifier.loadLines( "consumer/target/compile.txt", "UTF-8" );
        assertTrue( classpath.toString(), classpath.contains( "producer/test.jar" ) );
        assertTrue( classpath.toString(), classpath.contains( "producer/client.jar" ) );
    }

}