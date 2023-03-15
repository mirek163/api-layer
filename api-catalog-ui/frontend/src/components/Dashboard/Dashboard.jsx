/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
import { Typography, IconButton, Snackbar } from '@material-ui/core';
import { Alert } from '@mui/material';
import { Component } from 'react';
import SearchCriteria from '../Search/SearchCriteria';
import Shield from '../ErrorBoundary/Shield/Shield';
import './Dashboard.css';
import Tile from '../Tile/Tile';
import Spinner from '../Spinner/Spinner';
import formatError from '../Error/ErrorFormatter';
import ErrorDialog from '../Error/ErrorDialog';
import WizardContainer from '../Wizard/WizardContainer';
import DialogDropdown from '../Wizard/DialogDropdown';
import { enablerData } from '../Wizard/configs/wizard_onboarding_methods';
import ConfirmDialogContainer from '../Wizard/ConfirmDialogContainer';
import Image from '../../assets/images/background.svg';

export default class Dashboard extends Component {
    componentDidMount() {
        const { fetchTilesStart, clearService } = this.props;
        clearService();
        fetchTilesStart();
    }

    componentWillUnmount() {
        const { fetchTilesStop, clear } = this.props;
        clear();
        fetchTilesStop();
    }

    handleSearch = (value) => {
        const { filterText } = this.props;
        filterText(value);
    };

    refreshStaticApis = () => {
        const { refreshedStaticApi } = this.props;
        refreshedStaticApi();
    };

    toggleWizard = () => {
        const { wizardToggleDisplay } = this.props;
        wizardToggleDisplay();
    };

    handleClose = () => {
        const { closeAlert } = this.props;
        closeAlert();
    };

    setStyle(background, font) {
        localStorage.setItem('dashboardBackground', background);
        if (background) {
            document.body.style.backgroundColor = background;
        } else {
            document.body.style.backgroundColor = '#EFEFEF';
        }
        localStorage.setItem('fontFamily', font);
    }

    render() {
        const {
            tiles,
            history,
            searchCriteria,
            isLoading,
            fetchTilesError,
            fetchTilesStop,
            refreshedStaticApisError,
            clearError,
            authentication,
        } = this.props;
        let titleColor;
        let background;
        let font;
        const hasSearchCriteria = searchCriteria !== undefined && searchCriteria !== null && searchCriteria.length > 0;
        const hasTiles = !fetchTilesError && tiles && tiles.length > 0;
        if (hasTiles) {
            titleColor = tiles[0].titlesColor;
            background = tiles[0].dashboardBackgroundColor;
            font = tiles[0].font;
        }
        this.setStyle(background, font);
        let error = null;
        if (fetchTilesError !== undefined && fetchTilesError !== null) {
            fetchTilesStop();
            error = formatError(fetchTilesError);
        }
        let buttonColor = '#3272d9';
        if (localStorage.getItem('headerBackground')) {
            buttonColor = localStorage.getItem('headerBackground');
        }
        return (
            <div>
                <div id="dash-buttons">
                    <DialogDropdown
                        selectEnabler={this.props.selectEnabler}
                        data={enablerData}
                        toggleWizard={this.toggleWizard}
                        visible
                    />
                    <IconButton
                        id="refresh-api-button"
                        size="medium"
                        variant="outlined"
                        onClick={this.refreshStaticApis}
                        style={{ borderRadius: '0.1875em', color: buttonColor }}
                    >
                        Refresh Static APIs
                    </IconButton>
                </div>
                <WizardContainer />
                <Snackbar
                    anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
                    open={authentication.showUpdatePassSuccess}
                    onClose={this.handleClose}
                >
                    <Alert onClose={this.handleClose} severity="success" sx={{ width: '100%' }}>
                        Your mainframe password was sucessfully changed.
                    </Alert>
                </Snackbar>
                <ConfirmDialogContainer />
                <Spinner isLoading={isLoading} />
                {fetchTilesError && (
                    <div className="no-tiles-container">
                        <br />
                        <br />
                        <Typography data-testid="error" variant="subtitle1">
                            Tile details could not be retrieved, the following error was returned:
                        </Typography>
                        {error}
                    </div>
                )}
                <ErrorDialog refreshedStaticApisError={refreshedStaticApisError} clearError={clearError} />
                {!fetchTilesError && (
                    <div
                        className="apis"
                        style={background ? { backgroundColor: background } : { backgroundImage: `url(${Image})` }}
                    >
                        <div id="grid-container">
                            <div className="filtering-container">
                                <Shield title="Search Bar is broken !">
                                    <SearchCriteria placeholder="Search for APIs" doSearch={this.handleSearch} />
                                </Shield>
                                <h2 className="api-heading" style={{ color: titleColor }}>
                                    Available API services
                                </h2>
                            </div>
                            {hasTiles && tiles.map((tile) => <Tile key={tile.id} tile={tile} history={history} />)}
                            {!hasTiles && hasSearchCriteria && (
                                <Typography id="search_no_results" variant="subtitle2" style={{ color: '#1d5bbf' }}>
                                    No tiles found matching search criteria
                                </Typography>
                            )}
                        </div>
                    </div>
                )}
            </div>
        );
    }
}
