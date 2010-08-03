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
import org.apache.maven.it.util.FileUtils;
import org.apache.maven.it.util.ResourceExtractor;

import java.io.File;

/**
 * This is a test set for <a href="http://jira.codehaus.org/browse/MNG-4368">MNG-4368</a>.
 * 
 * @author Benjamin Bentmann
 */
public class MavenITmng4368TimestampAwareArtifactInstallerTest
    extends AbstractMavenIntegrationTestCase
{

    public MavenITmng4368TimestampAwareArtifactInstallerTest()
    {
        super( "[2.0.3,3.0-alpha-1),[3.0-alpha-6,)" );
    }

    /**
     * Verify that the artifact installer copies POMs to the local repo even if they have an older timestamp as the
     * copy in the local repo.
     */
    public void testitPomPackaging()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-4368/pom" );

        File aDir = new File( testDir, "branch-a" );
        File aPom = new File( aDir, "pom.xml" );
        File bDir = new File( testDir, "branch-b" );
        File bPom = new File( bDir, "pom.xml" );

        aPom.setLastModified( System.currentTimeMillis() );
        bPom.setLastModified( aPom.lastModified() - 1000 * 60 );

        Verifier verifier = newVerifier( aDir.getAbsolutePath() );
        verifier.setAutoclean( false );
        verifier.deleteDirectory( "target" );
        verifier.deleteArtifacts( "org.apache.maven.its.mng4368" );
        verifier.executeGoal( "initialize" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        File installedPom = new File( verifier.getArtifactPath( "org.apache.maven.its.mng4368", "test", "0.1-SNAPSHOT", "pom" ) );

        String pom = FileUtils.fileRead( installedPom, "UTF-8" );
        assertTrue( pom.indexOf( "Branch-A" ) > 0 );
        assertTrue( pom.indexOf( "Branch-B" ) < 0 );

        assertEquals( aPom.length(), bPom.length() );
        assertTrue( aPom.lastModified() > bPom.lastModified() );
        assertTrue( installedPom.lastModified() > bPom.lastModified() );

        verifier = newVerifier( bDir.getAbsolutePath() );
        verifier.setAutoclean( false );
        verifier.deleteDirectory( "target" );
        verifier.executeGoal( "initialize" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        pom = FileUtils.fileRead( installedPom, "UTF-8" );
        assertTrue( pom.indexOf( "Branch-A" ) < 0 );
        assertTrue( pom.indexOf( "Branch-B" ) > 0 );
    }

    /**
     * Verify that the artifact installer copies files to the local repo only if their timestamp differs from the copy
     * already in the local repo.
     */
    public void testitJarPackaging()
        throws Exception
    {
        requiresMavenVersion( "[2.2.2,3.0-alpha-1),[3.0-alpha-6,)" );

        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-4368/jar" );

        File aDir = new File( testDir, "branch-a" );
        File aArtifact = new File( aDir, "artifact.jar" );
        File bDir = new File( testDir, "branch-b" );
        File bArtifact = new File( bDir, "artifact.jar" );

        FileUtils.fileWrite( aArtifact.getPath(), "UTF-8", "from Branch-A" );
        aArtifact.setLastModified( System.currentTimeMillis() );
        FileUtils.fileWrite( bArtifact.getPath(), "UTF-8", "from Branch-B" );
        bArtifact.setLastModified( aArtifact.lastModified() - 1000 * 60 );

        Verifier verifier = newVerifier( aDir.getAbsolutePath() );
        verifier.setAutoclean( false );
        verifier.deleteDirectory( "target" );
        verifier.deleteArtifacts( "org.apache.maven.its.mng4368" );
        verifier.executeGoal( "initialize" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        File installedArtifact = new File( verifier.getArtifactPath( "org.apache.maven.its.mng4368", "test", "0.1-SNAPSHOT", "jar" ) );

        String data = FileUtils.fileRead( installedArtifact, "UTF-8" );
        assertTrue( data.indexOf( "Branch-A" ) > 0 );
        assertTrue( data.indexOf( "Branch-B" ) < 0 );

        assertEquals( aArtifact.length(), bArtifact.length() );
        assertTrue( aArtifact.lastModified() > bArtifact.lastModified() );
        assertTrue( installedArtifact.lastModified() > bArtifact.lastModified() );

        verifier = newVerifier( bDir.getAbsolutePath() );
        verifier.setAutoclean( false );
        verifier.deleteDirectory( "target" );
        verifier.executeGoal( "initialize" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        data = FileUtils.fileRead( installedArtifact, "UTF-8" );
        assertTrue( data.indexOf( "Branch-A" ) < 0 );
        assertTrue( data.indexOf( "Branch-B" ) > 0 );

        long lastModified = installedArtifact.lastModified();
        FileUtils.fileWrite( installedArtifact.getPath(), "UTF-8", "from Branch-C" );
        installedArtifact.setLastModified( lastModified );

        verifier = newVerifier( bDir.getAbsolutePath() );
        verifier.setAutoclean( false );
        verifier.deleteDirectory( "target" );
        verifier.setLogFileName( "log-b.txt" );
        verifier.executeGoal( "initialize" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        data = FileUtils.fileRead( installedArtifact, "UTF-8" );
        assertTrue( data.indexOf( "Branch-B" ) < 0 );
        assertTrue( data.indexOf( "Branch-C" ) > 0 );
    }

}
