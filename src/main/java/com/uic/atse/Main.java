package com.uic.atse;

import com.uic.atse.service.DevOpsOrchestration;

public class Main {

    public static void main(String[] args){

        DevOpsOrchestration orchestration = DevOpsOrchestration.getInstance();
        orchestration.execute();
    }
}
