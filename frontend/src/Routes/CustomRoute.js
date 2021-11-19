import {authenticationService as auth} from '../services/authenticationService';
import {Route, Redirect} from 'react-router-dom';
export const CustomRoute = ({ comp: Component, ...rest }) =>
  (
    <Route {...rest} render={(props) => {
       console.log(props.path);
       if(props.path === '/') auth.logout();
       return <Component {...props}/>
    }} />
  );