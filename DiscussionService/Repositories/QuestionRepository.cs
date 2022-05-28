using DiscussionService.Contracts;
using DiscussionService.Data;
using DiscussionService.Dtos;
using DiscussionService.Helpers;
using DiscussionService.Models;
using Microsoft.EntityFrameworkCore;

namespace DiscussionService.Repositories
{
    public class QuestionRepository : RepositoryBase<Question>, IQuestionRepository
    {
        public QuestionRepository(RepositoryContext repositoryContext) : base(repositoryContext)
        {

        }

        public void CreateQuestion(Question question)
        {
            Create(question);
        }

        public void DeleteQuestion(Question question)
        {
            Delete(question);
        }

        public async Task<PagedList<Question>> GetAllQuestionsAsync(QuestionsQueryStringParameters queryStringParameters)
        {

            var items = await FindAll()
                                   .Include(question => question.Student)
                                   .Include(question => question.Subject)
                                   .OrderByDescending(question => question.CreatedDate)
                                   .Skip((queryStringParameters.PageNumber - 1) * queryStringParameters.PageSize)
                                   .Take(queryStringParameters.PageSize)
                                   .ToListAsync();

            if (!string.IsNullOrWhiteSpace(queryStringParameters.Question))
            {
                items = items.Where(question =>
                                question.Title.ToLower().Contains(queryStringParameters.Question.Trim().ToLower())
                                || question.Content.ToLower().Contains(queryStringParameters.Question.Trim().ToLower()))
                                .ToList();
            }

            if (!string.IsNullOrWhiteSpace(queryStringParameters.Subject))
            {
                items = items.Where(question =>
                                        question.Subject.Name.Equals(queryStringParameters.Subject))
                                        .ToList();
            }

            return PagedList<Question>.ToPagedList(items, queryStringParameters.PageNumber, queryStringParameters.PageSize);
        }

        public async Task<Question> GetQuestionByIdAsync(Guid questionId)
        {
            return await FindByCondition(question => question.Id.Equals(questionId))
                            .Include(question => question.Student)
                            .Include(question => question.Subject)
                            .Include(_ => _.Answers.Where(answer => answer.Removed == false))
                            .FirstOrDefaultAsync();
        }

        public async Task<IEnumerable<Question>> GetQuestionsByStudentId(Guid studentId)
        {
            return await FindByCondition(question => question.StudentId.Equals(studentId))
                            .ToListAsync();
        }

        public async Task<IEnumerable<Question>> GetQuestionsRemovedByLecturerId(Guid lecturerId)
        {
            return await FindByCondition(question => question.RemovedBy.Equals(lecturerId))
                            .ToListAsync();
        }

        public void UpdateQuestion(Question question)
        {
            Update(question);
        }
    }
}