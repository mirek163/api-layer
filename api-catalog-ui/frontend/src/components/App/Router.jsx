/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

import { Redirect, Route, Router, Switch } from 'react-router-dom';
import BigShield from '../ErrorBoundary/BigShield/BigShield';
import { AsyncDashboardContainer, AsyncDetailPageContainer, AsyncLoginContainer } from './AsyncModules'; // eslint-disable-line import/no-cycle
import HeaderContainer from '../Header/HeaderContainer';
import Footer from '../Footer/Footer';
import PageNotFound from '../PageNotFound/PageNotFound';

// TODO required paramters: history

export default function createRouter(history) {
    return (
        <Router history={history}>
            <>
                <div className="content">
                    <Route path="/(dashboard|service/.*)/" component={HeaderContainer} />
                    <Switch>
                        <Route path="/" exact render={() => <Redirect replace to="/dashboard" />} />
                        <Route
                            path="/login"
                            exact
                            render={(props, state) => <AsyncLoginContainer {...props} {...state} />}
                        />
                        <Route
                            exact
                            path="/dashboard"
                            render={(props, state) => (
                                <BigShield>
                                    <AsyncDashboardContainer {...props} {...state} />
                                </BigShield>
                            )}
                        />
                        <Route
                            path="/service"
                            render={(props, state) => (
                                <BigShield history={history}>
                                    <AsyncDetailPageContainer {...props} {...state} />
                                </BigShield>
                            )}
                        />
                        <Route
                            render={(props, state) => (
                                <BigShield history={history}>
                                    <PageNotFound {...props} {...state} />
                                </BigShield>
                            )}
                        />
                    </Switch>
                </div>
                <Route path="/(dashboard|service/.*)/" component={Footer} />
            </>
        </Router>
    );
}
