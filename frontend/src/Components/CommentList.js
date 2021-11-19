import React, {Component, useState} from 'react';
import {Container, ListGroup, Button} from 'react-bootstrap';
import '../Styles/CommentBox.scss';
import Avatar from 'react-avatar';
import cn from 'classnames';
import moment from 'moment';
import ReactTimeout from 'react-timeout';
class CommentList extends Component {

    constructor(props) {
        super(props);
        this.commentListSorted = null;
        this.state = {isLoading: true, commentCount: 0}
        this.commentList = null;
    }

    refreshCommentList() {

        const { role, comments, icons, removeComment, count } = this.props;
        console.log(count, this.state.commentCount);
        if(count === this.state.commentCount) return;

        console.log("Refreshing the comment component",comments, icons, role)
        console.log("Icon dictionary keys", Object.keys(icons));
        this.commentListSorted = comments.sort(function(a,b) {
            return a.createdAt < b.createdAt ? -1 : 1;
        });
        var options = { month: 'long'};
        this.commentList = this.commentListSorted.map(val => {
            var options = { month: 'long'};
            var d = new Date( val.createdAt * 1000);
            var month = d.toLocaleDateString("en-US", options);
            var dateStr = month + ", " + moment(d).format("Do, YYYY, h:mm a");
            console.log("current comment in list", val, icons[val.posterUsername]);

            return (
                <div>
                {val.commentText ?
                     <ListGroup.Item>
                     {icons[val.posterUsername] ?
                         <Avatar round ={true}
                             borderRadius="50"
                             size="50"
                             name={val.posterName}
                             src={icons[val.posterUsername]}/>
                         :
                         <Avatar size="50"
                             round={true}
                             name={val.posterName}/>
                     }
                     {val.commentName}
                     <br/>
                     {dateStr}
                     <br/>
                     {val.commentText}
                     </ListGroup.Item> :
                     <div/>
                }
                {role === "ROLE_ADMIN" ?
                    <Button size="sm" color="danger" onClick={() => removeComment(val.id)}>Delete</Button>
                    : <div/>
                }
                </div>
            );
        });


    }
    componentDidMount() {

        this.refreshCommentList();

        this.setState({isLoading: false, commentCount: this.commentList.length});

    }

    render() {

        this.refreshCommentList();
        this.state.commentCount = this.commentList.length;
        if(this.state.isLoading) return (<div/>);
        return (
              <Container>
              <ListGroup>
                {this.commentList}
              </ListGroup>
              </Container>
        );
    }
}

export default ReactTimeout(CommentList);