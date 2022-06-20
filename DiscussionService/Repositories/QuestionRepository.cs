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
                                .OrderByDescending(question => question.CreatedDate)
                                // .Skip((queryStringParameters.PageNumber - 1) * queryStringParameters.PageSize)
                                // .Take(queryStringParameters.PageSize)
                                .Include(question => question.Student)
                                .Include(question => question.Subject)
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
                            .Include(question => question.Answers.Where(answer => answer.Removed == false)).ThenInclude(answer => answer.Student)
                            .FirstOrDefaultAsync();
        }

        public async Task<IEnumerable<Question>> GetQuestionsByStudentId(Guid studentId)
        {
            return await FindByCondition(question => question.StudentId.Equals(studentId))
                            .Include(question => question.Subject)
                            .Include(question => question.Student)
                            .OrderByDescending(question => question.CreatedDate)
                            .ToListAsync();
        }

        public async Task<IEnumerable<Question>> GetQuestionsRemovedByLecturer(string lecturerEmail)
        {
            return await FindByCondition(question => question.RemovedBy.Equals(lecturerEmail))
                            .ToListAsync();
        }

        public void UpdateQuestion(Question question)
        {
            Update(question);
        }
    }
}