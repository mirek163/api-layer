/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
import { Component, Suspense } from 'react';
import { ToastContainer } from 'react-toastify';
// eslint-disable-next-line import/no-cycle
import createRouter from './Router';
import BigShield from '../ErrorBoundary/BigShield/BigShield';
import ErrorContainer from '../Error/ErrorContainer';
import '../../assets/css/APIMReactToastify.css';
import Spinner from '../Spinner/Spinner';

class App extends Component {
    componentDidMount() {
        // workaround for missing process polyfill in webpack 5
        window.process = { ...window.process };
    }

    render() {
        const { history } = this.props;
        const isLoading = true;
        const r = createRouter(history);

        return (
            <div className="App">
                <BigShield history={history}>
                    <ToastContainer />
                    <ErrorContainer />
                    <Suspense fallback={<Spinner isLoading={isLoading} />}>{r}</Suspense>
                </BigShield>
            </div>
        );
    }
}

export default App;
