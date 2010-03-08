package org.jvnet.hudson.maven.plugins.hudson.util;

import org.ddsteps.mock.httpserver.JettyMockServer;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.security.Constraint;
import org.mortbay.jetty.security.ConstraintMapping;
import org.mortbay.jetty.security.HashUserRealm;
import org.mortbay.jetty.security.SecurityHandler;

public class JettyMockServerWithAuth extends JettyMockServer {

    public JettyMockServerWithAuth(Server server, String config) throws Exception {
        super(server);
        Constraint constraint = new Constraint();
        constraint.setName(Constraint.__BASIC_AUTH);
        constraint.setRoles(new String[] { "testrole" });
        constraint.setAuthenticate(true);

        ConstraintMapping cm = new ConstraintMapping();
        cm.setConstraint(constraint);
        cm.setPathSpec("/*");

        HashUserRealm realm = new HashUserRealm("testrealm", config);
        Handler existingHandler = server.getHandler();
        SecurityHandler sh = new SecurityHandler();
        sh.setUserRealm(realm);
        sh.setConstraintMappings(new ConstraintMapping[] { cm });
        sh.addHandler(existingHandler);
        sh.doStart();
        server.setHandler(sh);
    }
}