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
import { Menu, MenuItem } from '@material-ui/core';

export default class ServicesNavigationBar extends Component {
    render() {
        const { allContainers } = this.props;
        // eslint-disable-next-line no-console
        console.log(allContainers);
        // allContainers.forEach((services) => {
        //     // eslint-disable-next-line no-console
        //     console.log(services);
        // });
        return 'ciao';
        // return (
        // <div>
        //     <Menu
        //         id="sidebar-menu"
        //         keepMounted
        //     >
        //         {allContainers.map((itemType) => (
        //             <MenuItem key={itemType.text} onClick={this.handleClick}>
        //                 {itemType.text}
        //             </MenuItem>
        //         ))}
        //     </Menu>
        // </div>
        // );
    }
}
