#!/bin/bash
java -ea -Xms2m -cp 'lib/*' -Djava.security.manager -Djava.security.policy=dcvm.policy fr.sorbonne_u.components.cvm.utils.DCVMCyclicBarrier config.xml