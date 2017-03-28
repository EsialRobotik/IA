package fr.esialrobotik.data.table;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static org.junit.Assert.*;

/**
 * Created by icule on 28/03/17.
 */
public class TableTest {
    private Table table;

    public class TableModuleTest extends AbstractModule{
        protected void configure() {
            bind(Gson.class);
            bind(Table.class);
        }
    }

    @Before
    public void setUp(){
        Injector injector = Guice.createInjector(new TableModuleTest());
        injector.getInstance(Table.class);
    }

    @Test
    public void test(){

    }
}