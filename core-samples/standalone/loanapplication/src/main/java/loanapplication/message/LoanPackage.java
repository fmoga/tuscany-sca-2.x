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
package loanapplication.message;

/**
 * Contains information for a completed loan
 */
public interface LoanPackage {
    
    String getLoanNumber();

    void setLoanNumber(String loanNumber);

    int getType();

    void setType(int type);

    float getAmount();

    void setAmount(float amount);

    float getRate();

    void setRate(float rate);

    int getTerm();

    void setTerm(int term);
}
