    import React, {Component} from 'react';
import { Router, Route, Switch} from 'react-router-dom';
import Home from './Pages/Home';
import Posts from './Pages/Posts'
import Profile from './Pages/Profile'
import RegistrationSuccess from './Pages/RegistrationSuccess';
import RegistrationForm from './auth/RegistrationForm';
import LoginForm from './auth/LoginForm';
import UserModal from './Components/UserModal';

import PostEdit from "./Components/PostEdit";
import PostView from "./Components/PostView";
import AppNavbar from "./Components/AppNavbar";
import history from "./Components/history";
import {PrivateRoute} from './Routes/PrivateRoute';

export default class InnerApp extends React.PureComponent{

    render() {

        return (
            <div>

                <Router history={history}>
                  <div>
                  <AppNavbar/>
                  <Switch>
                    <Route path='/' exact={true} component={Home}/>
                    <PrivateRoute exact={true} path='/posts' comp={Posts}/>
                    <PrivateRoute exact={true} path='/posts/:id' comp={PostEdit}/>
                    <PrivateRoute path='/posts/view/:id' comp={PostView}/>
                    <PrivateRoute exact={true} path='/registration' comp={RegistrationSuccess}/>
                    <PrivateRoute exact={true} path='/profile' comp={Profile}/>
                  </Switch>
                  </div>
                </Router>

            </div>
        );
    }
}
//     <Toast onClose={this.setHideToast} show={this.state.showToast} delay={3000} autohide>
//                                  <Toast.Header>
//                                    <strong className="me-auto">Bootstrap</strong>
//                                    <small>11 mins ago</small>
//                                  </Toast.Header>
//                                  <Toast.Body>"Woohoo, you're reading this text in a Toast!"</Toast.Body>
//                             </Toast>