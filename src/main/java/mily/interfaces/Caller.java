package mily.interfaces;

import mily.parsing.*;

public interface Caller extends Named, CallSignatured, Typed {

    OperationNode getArg(int i);

    void setArg(int i, OperationNode operationNode);

    int getArgCount();
}
