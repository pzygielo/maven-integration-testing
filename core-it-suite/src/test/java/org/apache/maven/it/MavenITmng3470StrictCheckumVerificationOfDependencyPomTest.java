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

/**
 * This is a test set for <a href="http://jira.codehaus.org/browse/MNG-3740">MNG-3740</a>.
 * 
 * @author Benjamin Bentmann
 */
public class MavenITmng3470StrictCheckumVerificationOfDependencyPomTest
    extends AbstractMavenIntegrationTestCase
{

    public MavenITmng3470StrictCheckumVerificationOfDependencyPomTest()
    {
        super( "[2.0.3,2.0.4],[3.0-beta-1,)" );
    }

    /**
     * Verify that strict checksum verification fails the build in case a dependency POM is corrupt.
     */
    public void testit()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-3470" );

        Verifier verifier = newVerifier( testDir.getAbsolutePath() );
        verifier.setAutoclean( false );
        verifier.deleteDirectory( "target" );
        verifier.deleteArtifacts( "org.apache.maven.its.mng3470" );
        verifier.filterFile( "settings-template.xml", "settings.xml", "UTF-8", verifier.newDefaultFilterProperties() );
        verifier.getCliOptions().add( "--settings" );
        verifier.getCliOptions().add( "settings.xml" );
        try
        {
            try
            {
                verifier.setLogFileName( "log-1.txt" );
                verifier.executeGoal( "validate" );
                verifier.verifyErrorFreeLog();
                fail( "Build did not fail despite broken checksum of dependency POM." );
            }
            catch ( VerificationException e )
            {
                // expected
            }

            // NOTE: This second try is to make sure the state caching in the local repo properly replays the error
            try
            {
                verifier.setLogFileName( "log-2.txt" );
                verifier.executeGoal( "validate" );
                verifier.verifyErrorFreeLog();
                fail( "Build did not fail despite broken checksum of dependency POM." );
            }
            catch ( VerificationException e )
            {
                // expected
            }
        }
        finally
        {
            verifier.resetStreams();
        }
    }

}
