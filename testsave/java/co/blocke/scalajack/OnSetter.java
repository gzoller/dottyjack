package co.blocke.scalajack;

import co.blocke.dottyjack.*;

public class OnSetter {

    private int two;
    public int getTwo(){ return two; }
    @Change(name="dos") public void setTwo(int v) { two = v; }

}