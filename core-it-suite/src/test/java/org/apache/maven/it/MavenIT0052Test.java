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

public class MavenIT0052Test
    extends AbstractMavenIntegrationTestCase
{
    public MavenIT0052Test()
    {
        super( ALL_MAVEN_VERSIONS );
    }

    /**
     * Test that source attachment doesn't take place when
     * -DperformRelease=true is missing.
     */
    public void testit0052()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/it0052" );

        Verifier verifier = new Verifier( testDir.getAbsolutePath() );
        verifier.setAutoclean( false );
        verifier.deleteDirectory( "target" );
        verifier.executeGoal( "package" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        verifier.assertFilePresent( "target/jar-jar.txt" );
        verifier.assertFileNotPresent( "target/source-jar.txt" );
        verifier.assertFileNotPresent( "target/javadoc-jar.txt" );
    }

}
