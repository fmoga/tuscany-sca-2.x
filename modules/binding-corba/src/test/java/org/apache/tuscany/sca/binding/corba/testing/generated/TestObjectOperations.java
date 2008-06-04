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

package org.apache.tuscany.sca.binding.corba.testing.generated;


/**
* Tester/TestObjectOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from tester.idl
* pi�tek, 30 maj 2008 17:04:42 CEST
*/

public interface TestObjectOperations 
{
  org.apache.tuscany.sca.binding.corba.testing.generated.SomeStruct pickStructFromArgs (org.apache.tuscany.sca.binding.corba.testing.generated.SomeStructHolder arg1, org.apache.tuscany.sca.binding.corba.testing.generated.SomeStructHolder arg2, org.apache.tuscany.sca.binding.corba.testing.generated.SomeStructHolder arg3, int structNumber);
  org.apache.tuscany.sca.binding.corba.testing.generated.SomeStruct setStruct (org.apache.tuscany.sca.binding.corba.testing.generated.SomeStructHolder arg);
  org.apache.tuscany.sca.binding.corba.testing.generated.SimpleStruct setSimpleStruct (org.apache.tuscany.sca.binding.corba.testing.generated.SimpleStructHolder arg);
  int[] setLongSeq1 (org.apache.tuscany.sca.binding.corba.testing.generated.long_seq1Holder arg);
  int[][] setLongSeq2 (org.apache.tuscany.sca.binding.corba.testing.generated.long_seq2Holder arg);
  int[][][] setLongSeq3 (org.apache.tuscany.sca.binding.corba.testing.generated.long_seq3Holder arg);
} // interface TestObjectOperations
