import React, { Component, useState } from 'react';
import { Link, withRouter } from 'react-router-dom';
import axios from 'axios';
import { Button, Container, Card, CardHeader, CardTitle, CardBody, CardText, CardFooter, Label } from 'reactstrap';
import AppNavbar from './AppNavbar';
import CommentBox from './CommentBox';
import CommentList from './CommentList';
import {authHeader} from '../helpers/auth-header'
import moment from 'moment';
import {authenticationService as auth} from '../services/authenticationService';
import {getImageService as getImgSvc} from '../services/getImageService';
import {getNameService as getNameSvc} from '../services/getNameService';
import {convertFromRaw} from 'draft-js';
import renderHTML from 'react-render-html';
import {stateToHTML} from 'draft-js-export-html';

let commentCounter = 1;

class PostView extends Component {

    emptyItem = {
        title: '',
        date: '',
        body: '',
    };

    constructor(props) {
        super(props);
        this.currentRole = "ROLE_GUEST";
        this.state = {
            title: '',
            date: '',
            body: '',
            commentValue: '',
            comments: [],
            icons: {},
            currentCount: commentCounter,
            isExpanded : false,
            isLoading: true
        }

//                commentIcon: "",
//                commentName: "",
//                commentTimeStamp: "",
//                commentId:"",
//                text: "",
        this.handleCommentValue = this.handleCommentValue.bind(this);
        this.enterCommentLine = this.enterCommentLine.bind(this);
        this.submitCommentLine = this.submitCommentLine.bind(this);
        this.onCommentClose = this.onCommentClose.bind(this);
        this.setCommentLine = this.setCommentLine.bind(this);
        this.removeComment = this.removeComment.bind(this);
    }

    async componentDidMount() {
    //    console.log(this.props.match.params.id)
//        console.log(this.item);
        //console.log(this.props.match.params.id);


        const promise = auth.verifyLogin();
        if(promise) {
            promise.then(result => {
                const resolved = result;
                if(localStorage.getItem('currentUser')) this.getPostAndComments();
            })
        }

    }

    async getPostAndComments() {
        this.currentRole = getNameSvc.currentRoleValue.roleName;
            //console.log("ComponentDidMount currentRole", this.currentRole);
        var state = {
            title: "",
            date: "",
            body: "",
            comments: [],
            icons: {},
            currentCount: 0
        }
        await axios.get(`/api/posts/${this.props.match.params.id}`,
        {
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': authHeader()
            }
        }).then(response => {
        state.title = response.data.title;
        state.date = response.data.modifiedAt;
        state.body = response.data.body;
        state.comments = response.data.comments;
        //console.log(state.comments);

    }).catch(err => {console.log(err)});

    if(state.comments && state.comments[0]) {
    const usernameSet = new Set();
    const imageSet = new Set();

    state.comments.map(comment => {
            //console.log("Comment:", comment);
            state.currentCount++;

            usernameSet.add(comment.posterUsername)
    });

    var usernameArr = Array.from(usernameSet);
    var index = 0;
    console.log(usernameArr);
    await usernameArr.forEach(username => {
        axios.get(`/api/getImage/${username}`, {
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': authHeader()
            }

        }).then(response => {
            if(response.data){
                console.log("image received", response.data);
                var key = response.data.username;
                state.icons[key] = response.data.strBase64File;

            }
            index++;
            //call render when all images have been retrieved from the server
            if(index === usernameArr.length) {
                //update the commentCounter
                commentCounter = state.currentCount;
                this.setState({
                    title: state.title,
                    date: state.date,
                    body: state.body,
                    comments: state.comments,
                    icons: state.icons,
                    currentCount: state.currentCount,
                    isLoading: false
                });
            }

        });
    });
    console.log("state upon mount", state);
    //console.log("Icons dictionary key value pairs");
    //console.log(Object.entries(state.icons));

    } else {
    var imageData = getImgSvc.currentImageValue;
    state.icons[imageData.username] = imageData.strBase64File;
    //update the commentCounter
    commentCounter = state.currentCount;
    this.setState({
        title: state.title,
        date: state.date,
        body: state.body,
        comments: state.comments,
        icons: state.icons,
        currentCount: state.currentCount,
        isLoading: false
    });
    }

    }
    handleCommentValue = (e) => {
        this.setState({commentValue: e.target.value});
    }
    async setComment() {
        commentCounter++;
        var comment = {
            id: null,
            postId: this.props.match.params.id,
            posterName: getNameSvc.currentNameValue,
            posterUsername: auth.getUsernameFromJWT(),
            commentText: this.state.commentValue,
        }
        var savedComment = await this.submitComment(comment);
        var newComment = {
            id: savedComment.id,
            posterName: savedComment.posterName,
            posterUsername: savedComment.posterUsername,
            createdAt: savedComment.createdAt,
            commentText: savedComment.commentText,
        }
        if(this.state.comments) this.state.comments.push(newComment);
        else this.state.comment[0] = newComment;

        var img = getImgSvc.currentImageValue

        this.state.icons[savedComment.posterUsername] = img;
        this.setState({
          commentValue: ""
        });

    }
    setCommentLine() {
        //console.log("username",auth.getUsernameFromJWT());
        const promise = auth.verifyLogin();
        if(promise) {
            promise.then(result => {
                const resolved = result;
                if(localStorage.getItem('currentUser')) this.setComment();
            })
        } else console.log("Could not verify login credentials");



        //console.log("Upon comment submit", this.state.comments, this.state.icons);

    }

    async submitComment(comment) {
        var savedComment = "";

        const updatedComment = JSON.stringify(comment);
        //console.log("Comment to put", updatedComment);
        await axios({
            method: 'post',
            url: `/api/posts/comments/${this.props.match.params.id}`,
            data: updatedComment,
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': authHeader()
            }
        }).then(response => {
            savedComment = response.data;
        }).catch(err => {
            console.log("PutComment error", err);
        });
//        console.log("SubmitComment", savedComment);
        return savedComment;
    }

    async getIconImage(username) {
        await axios.get(`/api/getImage/${username}`, {
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': authHeader()
            }

        }).then(response => {

            return response.data.strBase64File;
        })
    }
    submitCommentLine = (e) => {
         e.preventDefault();
         this.setCommentLine();
    };
    enterCommentLine = (e) => {

         if (e.charCode === 13) {
          this.setCommentLine();
         }
    };

    onCommentClose = () => {

        this.setState({commentValue: "", isExpanded: false});
    }

    async removeComment(id) {
        var currComments = this.state.comments;
        var currCommentCount = 0;
        var index = 0;
        var commentIndex = 0;
        currComments.forEach(comment => {
            if(comment.id === id) commentIndex = index;
            index++;
        })

        if(index >= 0) {
            //update all other commentId's by decrementing higher id's by one

            if(currComments.length === 1) currComments = [];
            else {
                currComments.splice(commentIndex, 1);

            }
            commentCounter--;
            await axios.delete(`/api/posts/comments/${id}`, {
                headers: {'Authorization': authHeader()}

            }).then(response => {
                //console.log("removeComment successful");
                this.setState({comments: currComments})
            })
        }
        else console.log("Could not find comment to delete");

    }
//
//    handleChange(event) {
//        const target = event.target;
//        const value = target.value;
//        const name = target.name;
//        let item = {...this.state.item};
//        item[name] = value;
//        this.setState({item});
//    }


    render() {
        const {title, date, body, commentValue, comments, icons, isExpanded, isLoading} = this.state;
        //console.log("render ", comments);
        //console.log("Icon dictionary keys", Object.keys(icons));
        if(isLoading) { return <div/>}

        var bodyHTML = stateToHTML(convertFromRaw(JSON.parse(body)));
        var options = { month: 'long'};

        var currentRole = this.currentRole;
        var d = new Date( date * 1000);
        var month = d.toLocaleDateString("en-US", options);
        var dateStr = month + ", " + moment(d).format("Do, YYYY, h:mm a");
        return <div>
            <Container>
                <Card>
                <CardHeader>
                    <CardTitle>
                        <h2><em>{title}</em></h2>
                        {dateStr}
                    </CardTitle>

                </CardHeader>
                <CardBody>

                    <CardText>{renderHTML(bodyHTML)}</CardText>
                </CardBody>

                </Card>
                <CommentBox
                    commentValue = {commentValue}
                    handleCommentValue = {this.handleCommentValue}
                    enterCommentLine = {this.enterCommentLine}
                    submitCommentLine = {this.submitCommentLine}
                    onClose = {this.onCommentClose}
                    isExpanded = {this.isExpanded}
                 />
                 {(comments && comments[0]) ?
                    <div>

                        <CommentList
                            role = {currentRole}
                            comments={comments}
                            icons={icons}
                            removeComment = {this.removeComment}
                            count = {comments.length}
                            />

                    </div> :
                    <div/>
                 }
                 <Button color="primary" tag={Link} to="/posts">Return to Posts</Button>
            </Container>
        </div>
    }
}
export default withRouter(PostView);