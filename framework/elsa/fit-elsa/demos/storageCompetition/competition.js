import {presentation, presentationPage} from "../../plugins/presentation/presentation.js";

let competition = (div, title) => {
    let self = presentation(div, title);
    self.pageType = "competitionPage";
    return self;
}

let competitionPage = (div, graph, name) => {
    let self = presentationPage(div, graph, name);
    self.type = "competitionPage";

    // 动态的id与图形的关联关系
    self.comments = {};
    self.dynamicShapes = {};

    let parentHandleComment = self.handleComment;
    self.handleComment = (topic, s) => {
        // 统一计数
        let shapeId = topic.shape;
        if (self.comments[shapeId] === undefined) {
            self.comments[shapeId] = [];
        }
        self.comments[shapeId].push(topic.value);

        let shape = self.shapes.find(s => s.id === shapeId);
        if (shape) {
            parentHandleComment(topic, shape);
        } else {
            // 找到轮播后归属的表格，显示弹幕
            let dynamicShape = self.dynamicShapes[shapeId];
            if (dynamicShape) {
                self.showComment(dynamicShape, topic.value, true);
            }
        }
    }

    let parentLoadComment = self.loadComment;
    self.loadComment = comments => {
        console.log("loadComment", comments.socialValue)
        parentLoadComment(comments);
        let shapeId = comments.shape;
        if (self.comments[shapeId] === undefined) {
            self.comments[shapeId] = [];
        }
        comments.socialValue.forEach(s => self.comments[shapeId].push(s));
    }

    // let parentPublishComment = self.publishComment;
    // self.publishComment = comment => {
    //     console.log("publishComment", comment.value)
    //     parentPublishComment(comment);
    //     let shapeId = comment.shape;
    //     if (self.comments[shapeId] === undefined) self.comments[shapeId] = [];
    //     self.comments[shapeId].push(comment.value);
    // }

    return self;
}

export {competition, competitionPage};