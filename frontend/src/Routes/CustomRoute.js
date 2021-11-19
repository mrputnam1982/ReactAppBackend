import {authenticationService as auth} from '../services/authenticationService';
import {Route, Redirect} from 'react-router-dom';
export const CustomRoute = ({ comp: Component, ...rest }) =>
  (
    <Route {...rest} render={(props) => {
       console.log(props.path);
      return props.path === '/'
        ? ({auth.logout()}
        <Redirect to='/'/>)
        : <Component {...props}/>
    }} />
  );