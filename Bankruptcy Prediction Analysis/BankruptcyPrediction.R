bankruptcydata = read.csv("bankruptcyprediction.csv")

head(bankruptcydata)

str(bankruptcydata)

##Correlation matrix
library(ggcorrplot)


## Logistic regression
str(bankruptcydata)

library(caTools)
set.seed(2000)

# Split the data
spl = sample.split(bankruptcydata$Bankrupt., SplitRatio = 0.6)
train = subset(bankruptcydata, spl==TRUE)
test = subset(bankruptcydata, spl==FALSE)

table(bankruptcydata$Bankrupt.)

#Logistic regression
bankruptcylog = glm(Bankrupt. ~ ., data=train, family=binomial)
summary(bankruptcylog)

# Make predictions on test set
predictTest = predict(bankruptcylog, newdata = test, type="response")

# Confusion matrix for threshold of 0.5
table(test$Bankrupt., predictTest > 0.5)
(2612+17)/(2612+17+29+64)

# Test set AUC 
library(ROCR)
ROCRpred = prediction(predictTest, test$Bankrupt.)
performance(ROCRpred, "auc")@y.values

# Performance function
ROCRperf = performance(ROCRpred, "tpr", "fpr")

# Plot ROC curve
plot(ROCRperf)

# Add colors
plot(ROCRperf, colorize=TRUE)

#Find highly correlated variables
library(data.table)
corMatrix <- cor(bankruptcydata)
setDT(reshape2::melt(corMatrix))[order(value)]
set = setDT(reshape2::melt(corMatrix))[order(value)]


set[1:100]
set[8790:8925]



##Logistic regression- significant variables
bankruptcylogsmall = glm(Bankrupt. ~ Continuous.interest.rate..after.tax. + Operating.Expense.Rate + Net.Value.Per.Share..C.
                         + Borrowing.dependency + Retained.Earnings.to.Total.Assets + Cash.Turnover.Rate + Net.Income.to.Total.Assets
                           + Total.assets.to.GNP.price , data=train, family=binomial)
summary(bankruptcylogsmall)

# Make predictions on test set
predictTestSmall = predict(bankruptcylogsmall, newdata = test, type="response")

# Confusion matrix for threshold of 0.5
table(test$Bankrupt., predictTestSmall > 0.5)

# Test set AUC 
library(ROCR)
ROCRpred = prediction(predictTestSmall, test$Bankrupt.)
performance(ROCRpred, "auc")@y.values

# Performance function
ROCRperf = performance(ROCRpred, "tpr", "fpr")

# Plot ROC curve
plot(ROCRperf)

# Add colors
plot(ROCRperf, colorize=TRUE)


#subset bankruptcy data set
bankruptcydatasmall = bankruptcydata[c("Continuous.interest.rate..after.tax.", "Operating.Expense.Rate", "Net.Value.Per.Share..C."
                                       , "Borrowing.dependency" , "Retained.Earnings.to.Total.Assets", "Cash.Turnover.Rate", "Net.Income.to.Total.Assets"
                                       , "Total.assets.to.GNP.price")]




# Compute a correlation matrix
corr <- round(cor(bankruptcydatasmall), 2)

ggcorrplot(corr, lab = TRUE)





##Remove Retained.Earnings.to.Total.Assets from Logistic Regression model
bankruptcylogsmallest = glm(Bankrupt. ~ Continuous.interest.rate..after.tax. + Operating.Expense.Rate + Net.Value.Per.Share..C.
                         + Borrowing.dependency + Cash.Turnover.Rate + Net.Income.to.Total.Assets
                         + Total.assets.to.GNP.price , data=train, family=binomial)
summary(bankruptcylogsmallest)

# Make predictions on test set
predictTestSmallest = predict(bankruptcylogsmallest, newdata = test, type="response")

# Confusion matrix for threshold of 0.5
table(test$Bankrupt., predictTestSmallest > 0.5)

(2632+9)/(2632+9+79+8)

# Test set AUC 
library(ROCR)
ROCRpred = prediction(predictTestSmallest, test$Bankrupt.)
performance(ROCRpred, "auc")@y.values

# Performance function
ROCRperf = performance(ROCRpred, "tpr", "fpr")

# Plot ROC curve
plot(ROCRperf)

# Add colors
plot(ROCRperf, colorize=TRUE)

#mean(bankruptcylogsmall$residuals^2)

#with(summary(bankruptcylogsmall), 1 - deviance/null.deviance)



## CART model
library(rpart)
library(rpart.plot)

# CART model
BankruptcyTree = rpart(Bankrupt. ~ ., data = train, method="class", minbucket=50)

prp(BankruptcyTree)

# Make predictions
PredictCART = predict(BankruptcyTree, newdata = test, type = "class")
table(test$Bankrupt., PredictCART)

# ROC curve
library(ROCR)

PredictROC = predict(BankruptcyTree, newdata = test)
PredictROC

pred = prediction(PredictROC[,2], test$Bankrupt.)
perf = performance(pred, "tpr", "fpr")
plot(perf)

# Install cross-validation packages
#install.packages("caret")
library(caret)

# Define cross-validation experiment
numFolds = trainControl( method = "cv", number = 10 )
cpGrid = expand.grid( .cp = seq(0.01,0.5,0.01)) 

# Perform the cross validation
train(Bankrupt. ~ ., data = train, method = "rpart", trControl = numFolds, tuneGrid = cpGrid )

# Create a new CART model
BankruptcyTreeCV = rpart(Bankrupt. ~ ., data = train, method="class", cp = 0.04)

# Make predictions
PredictCV = predict(BankruptcyTreeCV, newdata = test, type = "class")
table(test$Bankrupt., PredictCV)
(59+64)/(59+18+29+64)

prp(BankruptcyTreeCV)

# ROC curve
library(ROCR)

PredictROC = predict(BankruptcyTreeCV, newdata = test)
PredictROC

pred = prediction(PredictROC[,2], test$Bankrupt.)
perf = performance(pred, "tpr", "fpr")
plot(perf, colorize=TRUE)
performance(pred, "auc")@y.values



##Random Forest
library(randomForest)
library(reprtree)
set.seed(2000)
#trainSmall = train[sample(nrow(train), 2000), ]

# Build random forest model
#BankruptcyForest = randomForest(Bankrupt. ~ ., data = trainSmall, ntree=200, nodesize=25 )

# Convert outcome to factor
train$Bankrupt. = as.factor(train$Bankrupt.)
test$Bankrupt. = as.factor(test$Bankrupt.)

# try again
BankruptcyForest = randomForest(Bankrupt. ~ ., data = train, ntree=200, nodesize=25 )

# Make predictions
PredictForest = predict(BankruptcyForest, newdata = test)
PredictForest
table(test$Bankrupt., PredictForest)

importance(BankruptcyForest)

reprtree:::plot.getTree(BankruptcyForest)


