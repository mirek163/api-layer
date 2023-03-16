/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

import { Component } from 'react';

export default class ServicesNavigationBar extends Component {
    render() {
        const { selectedService, tiles } = this.props;
        // eslint-disable-next-line no-console
        console.log(selectedService);
        // eslint-disable-next-line no-console
        console.log(tiles);
        return 'ciao';
    }
}
