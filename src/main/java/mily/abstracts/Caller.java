package mily.abstracts;

import mily.parsing.*;

public interface Caller extends Named, HasFunctionKey, Typed {

    OperationNode getArg(int i);

    void setArg(int i, OperationNode operationNode);

    int getArgCount();
}
