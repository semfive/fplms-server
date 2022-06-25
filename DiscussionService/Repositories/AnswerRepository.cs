using DiscussionService.Contracts;
using DiscussionService.Data;
using DiscussionService.Dtos;
using DiscussionService.Helpers;
using DiscussionService.Models;
using Microsoft.EntityFrameworkCore;

namespace DiscussionService.Repositories
{
    public class AnswerRepository : RepositoryBase<Answer>, IAnswerRepository
    {
        public AnswerRepository(RepositoryContext repositoryContext) : base(repositoryContext)
        {

        }

        public void CreateAnswer(Answer answer)
        {
            Create(answer);
        }

        public void DeleteAnswer(Answer answer)
        {
            Delete(answer);
        }

        public async Task<PagedList<Answer>> GetAllAnswersAsync(AnswersQueryStringParameters answersQueryStringParameters)
        {
            var items = await FindAll()
                                .Include(answer => answer.Student)
                                .OrderByDescending(answer => answer.CreatedDate)
                                .ToListAsync();

            return PagedList<Answer>.ToPagedList(items, answersQueryStringParameters.PageNumber, answersQueryStringParameters.PageSize);
        }

        public async Task<Answer> GetAnswerByIdAsync(Guid answerId)
        {
            return await FindByCondition(answer => answer.Id.Equals(answerId)).Include(answer => answer.Upvoters).FirstOrDefaultAsync();
        }

        public async Task<IEnumerable<Answer>> GetAnswersByStudentId(Guid studentId)
        {
            return await FindByCondition(answer => answer.StudentId.Equals(studentId))
                            .Include(question => question.Student)
                            .OrderByDescending(answer => answer.CreatedDate)
                            .ToListAsync();
        }

        public async Task<IEnumerable<Answer>> GetAnswersRemovedByLecturer(string lecturerEmail)
        {
            return await FindAll().Where(answer => answer.RemovedBy.Equals(lecturerEmail))
                            .Include(answer => answer.Student)
                            .Include(answer => answer.Question)
                            .ToListAsync();
        }

        public void UpdateAnswer(Answer answer)
        {
            Update(answer);
        }
    }
}